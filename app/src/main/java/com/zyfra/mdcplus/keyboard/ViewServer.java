package com.zyfra.mdcplus.keyboard;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ViewServer implements Runnable {
    private static final String BUILD_TYPE_USER = "user";

    private static final String COMMAND_PROTOCOL_VERSION = "PROTOCOL";

    private static final String COMMAND_SERVER_VERSION = "SERVER";

    private static final String COMMAND_WINDOW_MANAGER_AUTOLIST = "AUTOLIST";

    private static final String COMMAND_WINDOW_MANAGER_GET_FOCUS = "GET_FOCUS";

    private static final String COMMAND_WINDOW_MANAGER_LIST = "LIST";

    private static final String LOG_TAG = "LocalViewServer";

    private static final String VALUE_PROTOCOL_VERSION = "4";

    private static final String VALUE_SERVER_VERSION = "4";

    private static final int VIEW_SERVER_DEFAULT_PORT = 4939;

    private static final int VIEW_SERVER_MAX_CONNECTIONS = 10;

    private static ViewServer sServer;

    private final ReentrantReadWriteLock mFocusLock = new ReentrantReadWriteLock();

    private View mFocusedWindow;

    private final List<WindowListener> mListeners = new CopyOnWriteArrayList<WindowListener>();

    private final int mPort = -1;

    private ServerSocket mServer;

    private Thread mThread;

    private ExecutorService mThreadPool;

    private final HashMap<View, String> mWindows = new HashMap<View, String>();

    private final ReentrantReadWriteLock mWindowsLock = new ReentrantReadWriteLock();

    private ViewServer() {}

    private ViewServer(int paramInt) {}

    private void addWindowListener(WindowListener paramWindowListener) {
        if (!this.mListeners.contains(paramWindowListener))
            this.mListeners.add(paramWindowListener);
    }

    private void fireFocusChangedEvent() {
        Iterator<WindowListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext())
            ((WindowListener)iterator.next()).focusChanged();
    }

    private void fireWindowsChangedEvent() {
        Iterator<WindowListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext())
            ((WindowListener)iterator.next()).windowsChanged();
    }

    public static ViewServer get(Context paramContext) {
        ApplicationInfo applicationInfo = paramContext.getApplicationInfo();
        if ("user".equals(Build.TYPE) && (applicationInfo.flags & 0x2) != 0) {
            if (sServer == null)
                sServer = new ViewServer(4939);
            if (!sServer.isRunning())
                try {
                    sServer.start();
                } catch (IOException iOException) {
                    Log.d("LocalViewServer", "Error:", iOException);
                }
            return sServer;
        }
        sServer = new NoopViewServer();
        return sServer;
    }

    private void removeWindowListener(WindowListener paramWindowListener) {
        this.mListeners.remove(paramWindowListener);
    }

    private static boolean writeValue(Socket paramSocket, String paramString) {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: aconst_null
        //   3: astore #4
        //   5: new java/io/BufferedWriter
        //   8: dup
        //   9: new java/io/OutputStreamWriter
        //   12: dup
        //   13: aload_0
        //   14: invokevirtual getOutputStream : ()Ljava/io/OutputStream;
        //   17: invokespecial <init> : (Ljava/io/OutputStream;)V
        //   20: sipush #8192
        //   23: invokespecial <init> : (Ljava/io/Writer;I)V
        //   26: astore_0
        //   27: aload_0
        //   28: aload_1
        //   29: invokevirtual write : (Ljava/lang/String;)V
        //   32: aload_0
        //   33: ldc '\\n'
        //   35: invokevirtual write : (Ljava/lang/String;)V
        //   38: aload_0
        //   39: invokevirtual flush : ()V
        //   42: iconst_1
        //   43: istore_2
        //   44: aload_0
        //   45: ifnull -> 105
        //   48: aload_0
        //   49: invokevirtual close : ()V
        //   52: iload_2
        //   53: ireturn
        //   54: astore_0
        //   55: iconst_0
        //   56: ireturn
        //   57: astore_0
        //   58: aload #4
        //   60: astore_0
        //   61: iconst_0
        //   62: istore_2
        //   63: aload_0
        //   64: ifnull -> 52
        //   67: aload_0
        //   68: invokevirtual close : ()V
        //   71: iconst_0
        //   72: ireturn
        //   73: astore_0
        //   74: iconst_0
        //   75: ireturn
        //   76: astore_0
        //   77: aload_3
        //   78: astore_1
        //   79: aload_1
        //   80: ifnull -> 87
        //   83: aload_1
        //   84: invokevirtual close : ()V
        //   87: aload_0
        //   88: athrow
        //   89: astore_1
        //   90: goto -> 87
        //   93: astore_3
        //   94: aload_0
        //   95: astore_1
        //   96: aload_3
        //   97: astore_0
        //   98: goto -> 79
        //   101: astore_1
        //   102: goto -> 61
        //   105: iconst_1
        //   106: ireturn
        // Exception table:
        //   from	to	target	type
        //   5	27	57	java/lang/Exception
        //   5	27	76	finally
        //   27	42	101	java/lang/Exception
        //   27	42	93	finally
        //   48	52	54	java/io/IOException
        //   67	71	73	java/io/IOException
        //   83	87	89	java/io/IOException
    }

    public void addWindow(Activity paramActivity) {
        String str = paramActivity.getTitle().toString();
        if (TextUtils.isEmpty(str)) {
            str = paramActivity.getClass().getCanonicalName() + "/0x" + System.identityHashCode(paramActivity);
        } else {
            str = str + "(" + paramActivity.getClass().getCanonicalName() + ")";
        }
        addWindow(paramActivity.getWindow().getDecorView(), str);
    }

    public void addWindow(View paramView, String paramString) {
        this.mWindowsLock.writeLock().lock();
        try {
            this.mWindows.put(paramView.getRootView(), paramString);
            this.mWindowsLock.writeLock().unlock();
            return;
        } finally {
            this.mWindowsLock.writeLock().unlock();
        }
    }

    public boolean isRunning() {
        return (this.mThread != null && this.mThread.isAlive());
    }

    public void removeWindow(Activity paramActivity) {
        removeWindow(paramActivity.getWindow().getDecorView());
    }

    public void removeWindow(View paramView) {
        this.mWindowsLock.writeLock().lock();
        try {
            this.mWindows.remove(paramView.getRootView());
            this.mWindowsLock.writeLock().unlock();
            return;
        } finally {
            this.mWindowsLock.writeLock().unlock();
        }
    }

    public void run() {
        // Byte code:
        //   0: aload_0
        //   1: new java/net/ServerSocket
        //   4: dup
        //   5: aload_0
        //   6: getfield mPort : I
        //   9: bipush #10
        //   11: invokestatic getLocalHost : ()Ljava/net/InetAddress;
        //   14: invokespecial <init> : (IILjava/net/InetAddress;)V
        //   17: putfield mServer : Ljava/net/ServerSocket;
        //   20: invokestatic currentThread : ()Ljava/lang/Thread;
        //   23: aload_0
        //   24: getfield mThread : Ljava/lang/Thread;
        //   27: if_acmpne -> 110
        //   30: aload_0
        //   31: getfield mServer : Ljava/net/ServerSocket;
        //   34: invokevirtual accept : ()Ljava/net/Socket;
        //   37: astore_1
        //   38: aload_0
        //   39: getfield mThreadPool : Ljava/util/concurrent/ExecutorService;
        //   42: ifnull -> 95
        //   45: aload_0
        //   46: getfield mThreadPool : Ljava/util/concurrent/ExecutorService;
        //   49: new ru/androidteam/rukeyboard/ViewServer$ViewServerWorker
        //   52: dup
        //   53: aload_0
        //   54: aload_1
        //   55: invokespecial <init> : (Lru/androidteam/rukeyboard/ViewServer;Ljava/net/Socket;)V
        //   58: invokeinterface submit : (Ljava/lang/Runnable;)Ljava/util/concurrent/Future;
        //   63: pop
        //   64: goto -> 20
        //   67: astore_1
        //   68: ldc 'LocalViewServer'
        //   70: ldc_w 'Connection error: '
        //   73: aload_1
        //   74: invokestatic w : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   77: pop
        //   78: goto -> 20
        //   81: astore_1
        //   82: ldc 'LocalViewServer'
        //   84: ldc_w 'Starting ServerSocket error: '
        //   87: aload_1
        //   88: invokestatic w : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   91: pop
        //   92: goto -> 20
        //   95: aload_1
        //   96: invokevirtual close : ()V
        //   99: goto -> 20
        //   102: astore_1
        //   103: aload_1
        //   104: invokevirtual printStackTrace : ()V
        //   107: goto -> 20
        //   110: return
        // Exception table:
        //   from	to	target	type
        //   0	20	81	java/lang/Exception
        //   30	64	67	java/lang/Exception
        //   95	99	102	java/io/IOException
        //   95	99	67	java/lang/Exception
        //   103	107	67	java/lang/Exception
    }

    public void setFocusedWindow(Activity paramActivity) {
        setFocusedWindow(paramActivity.getWindow().getDecorView());
    }

    public void setFocusedWindow(View paramView) {
        this.mFocusLock.writeLock().lock();
        if (paramView == null) {
            paramView = null;
        } else {
            paramView = paramView.getRootView();
        }
        try {
            this.mFocusedWindow = paramView;
            this.mFocusLock.writeLock().unlock();
            return;
        } finally {
            this.mFocusLock.writeLock().unlock();
        }
    }

    public boolean start() throws IOException {
        if (this.mThread != null)
            return false;
        this.mThread = new Thread(this, "Local View Server [port=" + this.mPort + "]");
        this.mThreadPool = Executors.newFixedThreadPool(10);
        this.mThread.start();
        return true;
    }

    public boolean stop() {
        if (this.mThread != null) {
            this.mThread.interrupt();
            if (this.mThreadPool != null)
                try {
                    this.mThreadPool.shutdownNow();
                } catch (SecurityException securityException) {
                    Log.w("LocalViewServer", "Could not stop all view server threads");
                }
            this.mThreadPool = null;
            this.mThread = null;
            try {
                this.mServer.close();
                this.mServer = null;
                return true;
            } catch (IOException iOException) {
                Log.w("LocalViewServer", "Could not close the view server");
            }
        }
        this.mWindowsLock.writeLock().lock();
        try {
            this.mWindows.clear();
            this.mWindowsLock.writeLock().unlock();
            this.mFocusLock.writeLock().lock();
        } finally {
            this.mWindowsLock.writeLock().unlock();
        }
    }

    private static class NoopViewServer extends ViewServer {
        private NoopViewServer() {}

        public void addWindow(Activity param1Activity) {}

        public void addWindow(View param1View, String param1String) {}

        public boolean isRunning() {
            return false;
        }

        public void removeWindow(Activity param1Activity) {}

        public void removeWindow(View param1View) {}

        public void run() {}

        public void setFocusedWindow(Activity param1Activity) {}

        public void setFocusedWindow(View param1View) {}

        public boolean start() throws IOException {
            return false;
        }

        public boolean stop() {
            return false;
        }
    }

    private static class UncloseableOuputStream extends OutputStream {
        private final OutputStream mStream;

        UncloseableOuputStream(OutputStream param1OutputStream) {
            this.mStream = param1OutputStream;
        }

        public void close() throws IOException {}

        public boolean equals(Object param1Object) {
            return this.mStream.equals(param1Object);
        }

        public void flush() throws IOException {
            this.mStream.flush();
        }

        public int hashCode() {
            return this.mStream.hashCode();
        }

        public String toString() {
            return this.mStream.toString();
        }

        public void write(int param1Int) throws IOException {
            this.mStream.write(param1Int);
        }

        public void write(byte[] param1ArrayOfbyte) throws IOException {
            this.mStream.write(param1ArrayOfbyte);
        }

        public void write(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
            this.mStream.write(param1ArrayOfbyte, param1Int1, param1Int2);
        }
    }

    private class ViewServerWorker implements Runnable, WindowListener {
        private Socket mClient;

        private final Object[] mLock = new Object[0];

        private boolean mNeedFocusedWindowUpdate;

        private boolean mNeedWindowListUpdate;

        public ViewServerWorker(Socket param1Socket) {
            this.mClient = param1Socket;
            this.mNeedWindowListUpdate = false;
            this.mNeedFocusedWindowUpdate = false;
        }

        private View findWindow(int param1Int) {
            if (param1Int == -1) {
                ViewServer.this.mWindowsLock.readLock().lock();
                try {
                    return ViewServer.this.mFocusedWindow;
                } finally {
                    ViewServer.this.mWindowsLock.readLock().unlock();
                }
            }
            ViewServer.this.mWindowsLock.readLock().lock();
            try {
                Iterator<Map.Entry> iterator = ViewServer.this.mWindows.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = iterator.next();
                    if (System.identityHashCode(entry.getKey()) == param1Int)
                        return (View)entry.getKey();
                }
                return null;
            } finally {
                ViewServer.this.mWindowsLock.readLock().unlock();
            }
        }

        private boolean getFocusedWindow(Socket param1Socket) {
            // Byte code:
            //   0: iconst_1
            //   1: istore_2
            //   2: aconst_null
            //   3: astore #4
            //   5: aconst_null
            //   6: astore_3
            //   7: new java/io/BufferedWriter
            //   10: dup
            //   11: new java/io/OutputStreamWriter
            //   14: dup
            //   15: aload_1
            //   16: invokevirtual getOutputStream : ()Ljava/io/OutputStream;
            //   19: invokespecial <init> : (Ljava/io/OutputStream;)V
            //   22: sipush #8192
            //   25: invokespecial <init> : (Ljava/io/Writer;I)V
            //   28: astore_1
            //   29: aload_0
            //   30: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   33: invokestatic access$600 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
            //   36: invokevirtual readLock : ()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
            //   39: invokevirtual lock : ()V
            //   42: aload_0
            //   43: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   46: invokestatic access$400 : (Lru/androidteam/rukeyboard/ViewServer;)Landroid/view/View;
            //   49: astore_3
            //   50: aload_0
            //   51: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   54: invokestatic access$600 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
            //   57: invokevirtual readLock : ()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
            //   60: invokevirtual unlock : ()V
            //   63: aload_3
            //   64: ifnull -> 139
            //   67: aload_0
            //   68: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   71: invokestatic access$300 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
            //   74: invokevirtual readLock : ()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
            //   77: invokevirtual lock : ()V
            //   80: aload_0
            //   81: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   84: invokestatic access$500 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/HashMap;
            //   87: aload_0
            //   88: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   91: invokestatic access$400 : (Lru/androidteam/rukeyboard/ViewServer;)Landroid/view/View;
            //   94: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
            //   97: checkcast java/lang/String
            //   100: astore #4
            //   102: aload_0
            //   103: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   106: invokestatic access$300 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
            //   109: invokevirtual readLock : ()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
            //   112: invokevirtual unlock : ()V
            //   115: aload_1
            //   116: aload_3
            //   117: invokestatic identityHashCode : (Ljava/lang/Object;)I
            //   120: invokestatic toHexString : (I)Ljava/lang/String;
            //   123: invokevirtual write : (Ljava/lang/String;)V
            //   126: aload_1
            //   127: bipush #32
            //   129: invokevirtual write : (I)V
            //   132: aload_1
            //   133: aload #4
            //   135: invokevirtual append : (Ljava/lang/CharSequence;)Ljava/io/Writer;
            //   138: pop
            //   139: aload_1
            //   140: bipush #10
            //   142: invokevirtual write : (I)V
            //   145: aload_1
            //   146: invokevirtual flush : ()V
            //   149: aload_1
            //   150: ifnull -> 244
            //   153: aload_1
            //   154: invokevirtual close : ()V
            //   157: iload_2
            //   158: ireturn
            //   159: astore_3
            //   160: aload_0
            //   161: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   164: invokestatic access$600 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
            //   167: invokevirtual readLock : ()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
            //   170: invokevirtual unlock : ()V
            //   173: aload_3
            //   174: athrow
            //   175: astore_3
            //   176: iconst_0
            //   177: istore_2
            //   178: aload_1
            //   179: ifnull -> 157
            //   182: aload_1
            //   183: invokevirtual close : ()V
            //   186: iconst_0
            //   187: ireturn
            //   188: astore_1
            //   189: iconst_0
            //   190: ireturn
            //   191: astore_3
            //   192: aload_0
            //   193: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   196: invokestatic access$300 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
            //   199: invokevirtual readLock : ()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
            //   202: invokevirtual unlock : ()V
            //   205: aload_3
            //   206: athrow
            //   207: astore #4
            //   209: aload_1
            //   210: astore_3
            //   211: aload #4
            //   213: astore_1
            //   214: aload_3
            //   215: ifnull -> 222
            //   218: aload_3
            //   219: invokevirtual close : ()V
            //   222: aload_1
            //   223: athrow
            //   224: astore_1
            //   225: iconst_0
            //   226: ireturn
            //   227: astore_3
            //   228: goto -> 222
            //   231: astore_1
            //   232: aload #4
            //   234: astore_3
            //   235: goto -> 214
            //   238: astore_1
            //   239: aload_3
            //   240: astore_1
            //   241: goto -> 176
            //   244: iconst_1
            //   245: ireturn
            // Exception table:
            //   from	to	target	type
            //   7	29	238	java/lang/Exception
            //   7	29	231	finally
            //   29	42	175	java/lang/Exception
            //   29	42	207	finally
            //   42	50	159	finally
            //   50	63	175	java/lang/Exception
            //   50	63	207	finally
            //   67	80	175	java/lang/Exception
            //   67	80	207	finally
            //   80	102	191	finally
            //   102	139	175	java/lang/Exception
            //   102	139	207	finally
            //   139	149	175	java/lang/Exception
            //   139	149	207	finally
            //   153	157	224	java/io/IOException
            //   160	175	175	java/lang/Exception
            //   160	175	207	finally
            //   182	186	188	java/io/IOException
            //   192	207	175	java/lang/Exception
            //   192	207	207	finally
            //   218	222	227	java/io/IOException
        }

        private boolean listWindows(Socket param1Socket) {
            // Byte code:
            //   0: aconst_null
            //   1: astore_2
            //   2: aconst_null
            //   3: astore_3
            //   4: aload_0
            //   5: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   8: invokestatic access$300 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
            //   11: invokevirtual readLock : ()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
            //   14: invokevirtual lock : ()V
            //   17: new java/io/BufferedWriter
            //   20: dup
            //   21: new java/io/OutputStreamWriter
            //   24: dup
            //   25: aload_1
            //   26: invokevirtual getOutputStream : ()Ljava/io/OutputStream;
            //   29: invokespecial <init> : (Ljava/io/OutputStream;)V
            //   32: sipush #8192
            //   35: invokespecial <init> : (Ljava/io/Writer;I)V
            //   38: astore_1
            //   39: aload_0
            //   40: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   43: invokestatic access$500 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/HashMap;
            //   46: invokevirtual entrySet : ()Ljava/util/Set;
            //   49: invokeinterface iterator : ()Ljava/util/Iterator;
            //   54: astore_2
            //   55: aload_2
            //   56: invokeinterface hasNext : ()Z
            //   61: ifeq -> 143
            //   64: aload_2
            //   65: invokeinterface next : ()Ljava/lang/Object;
            //   70: checkcast java/util/Map$Entry
            //   73: astore_3
            //   74: aload_1
            //   75: aload_3
            //   76: invokeinterface getKey : ()Ljava/lang/Object;
            //   81: invokestatic identityHashCode : (Ljava/lang/Object;)I
            //   84: invokestatic toHexString : (I)Ljava/lang/String;
            //   87: invokevirtual write : (Ljava/lang/String;)V
            //   90: aload_1
            //   91: bipush #32
            //   93: invokevirtual write : (I)V
            //   96: aload_1
            //   97: aload_3
            //   98: invokeinterface getValue : ()Ljava/lang/Object;
            //   103: checkcast java/lang/CharSequence
            //   106: invokevirtual append : (Ljava/lang/CharSequence;)Ljava/io/Writer;
            //   109: pop
            //   110: aload_1
            //   111: bipush #10
            //   113: invokevirtual write : (I)V
            //   116: goto -> 55
            //   119: astore_2
            //   120: aload_0
            //   121: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   124: invokestatic access$300 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
            //   127: invokevirtual readLock : ()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
            //   130: invokevirtual unlock : ()V
            //   133: aload_1
            //   134: ifnull -> 141
            //   137: aload_1
            //   138: invokevirtual close : ()V
            //   141: iconst_0
            //   142: ireturn
            //   143: aload_1
            //   144: ldc 'DONE.\\n'
            //   146: invokevirtual write : (Ljava/lang/String;)V
            //   149: aload_1
            //   150: invokevirtual flush : ()V
            //   153: aload_0
            //   154: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   157: invokestatic access$300 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
            //   160: invokevirtual readLock : ()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
            //   163: invokevirtual unlock : ()V
            //   166: aload_1
            //   167: ifnull -> 224
            //   170: aload_1
            //   171: invokevirtual close : ()V
            //   174: iconst_1
            //   175: ireturn
            //   176: astore_1
            //   177: iconst_0
            //   178: ireturn
            //   179: astore_1
            //   180: iconst_0
            //   181: ireturn
            //   182: astore_1
            //   183: aload_0
            //   184: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   187: invokestatic access$300 : (Lru/androidteam/rukeyboard/ViewServer;)Ljava/util/concurrent/locks/ReentrantReadWriteLock;
            //   190: invokevirtual readLock : ()Ljava/util/concurrent/locks/ReentrantReadWriteLock$ReadLock;
            //   193: invokevirtual unlock : ()V
            //   196: aload_2
            //   197: ifnull -> 204
            //   200: aload_2
            //   201: invokevirtual close : ()V
            //   204: aload_1
            //   205: athrow
            //   206: astore_2
            //   207: goto -> 204
            //   210: astore_3
            //   211: aload_1
            //   212: astore_2
            //   213: aload_3
            //   214: astore_1
            //   215: goto -> 183
            //   218: astore_1
            //   219: aload_3
            //   220: astore_1
            //   221: goto -> 120
            //   224: iconst_1
            //   225: ireturn
            // Exception table:
            //   from	to	target	type
            //   4	39	218	java/lang/Exception
            //   4	39	182	finally
            //   39	55	119	java/lang/Exception
            //   39	55	210	finally
            //   55	116	119	java/lang/Exception
            //   55	116	210	finally
            //   137	141	179	java/io/IOException
            //   143	153	119	java/lang/Exception
            //   143	153	210	finally
            //   170	174	176	java/io/IOException
            //   200	204	206	java/io/IOException
        }

        private boolean windowCommand(Socket param1Socket, String param1String1, String param1String2) {
            // Byte code:
            //   0: iconst_1
            //   1: istore #7
            //   3: aconst_null
            //   4: astore #11
            //   6: aconst_null
            //   7: astore #12
            //   9: aconst_null
            //   10: astore #10
            //   12: aload_3
            //   13: astore #9
            //   15: aload #12
            //   17: astore #8
            //   19: aload_3
            //   20: bipush #32
            //   22: invokevirtual indexOf : (I)I
            //   25: istore #5
            //   27: iload #5
            //   29: istore #4
            //   31: iload #5
            //   33: iconst_m1
            //   34: if_icmpne -> 50
            //   37: aload_3
            //   38: astore #9
            //   40: aload #12
            //   42: astore #8
            //   44: aload_3
            //   45: invokevirtual length : ()I
            //   48: istore #4
            //   50: aload_3
            //   51: astore #9
            //   53: aload #12
            //   55: astore #8
            //   57: aload_3
            //   58: iconst_0
            //   59: iload #4
            //   61: invokevirtual substring : (II)Ljava/lang/String;
            //   64: bipush #16
            //   66: invokestatic parseLong : (Ljava/lang/String;I)J
            //   69: l2i
            //   70: istore #5
            //   72: aload_3
            //   73: astore #9
            //   75: aload #12
            //   77: astore #8
            //   79: iload #4
            //   81: aload_3
            //   82: invokevirtual length : ()I
            //   85: if_icmpge -> 138
            //   88: aload_3
            //   89: astore #9
            //   91: aload #12
            //   93: astore #8
            //   95: aload_3
            //   96: iload #4
            //   98: iconst_1
            //   99: iadd
            //   100: invokevirtual substring : (I)Ljava/lang/String;
            //   103: astore_3
            //   104: aload_3
            //   105: astore #9
            //   107: aload #12
            //   109: astore #8
            //   111: aload_0
            //   112: iload #5
            //   114: invokespecial findWindow : (I)Landroid/view/View;
            //   117: astore #13
            //   119: aload #13
            //   121: ifnonnull -> 144
            //   124: iconst_0
            //   125: ifeq -> 136
            //   128: new java/lang/NullPointerException
            //   131: dup
            //   132: invokespecial <init> : ()V
            //   135: athrow
            //   136: iconst_0
            //   137: ireturn
            //   138: ldc ''
            //   140: astore_3
            //   141: goto -> 104
            //   144: aload_3
            //   145: astore #9
            //   147: aload #12
            //   149: astore #8
            //   151: ldc android/view/ViewDebug
            //   153: ldc 'dispatchCommand'
            //   155: iconst_4
            //   156: anewarray java/lang/Class
            //   159: dup
            //   160: iconst_0
            //   161: ldc android/view/View
            //   163: aastore
            //   164: dup
            //   165: iconst_1
            //   166: ldc java/lang/String
            //   168: aastore
            //   169: dup
            //   170: iconst_2
            //   171: ldc java/lang/String
            //   173: aastore
            //   174: dup
            //   175: iconst_3
            //   176: ldc java/io/OutputStream
            //   178: aastore
            //   179: invokevirtual getDeclaredMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
            //   182: astore #14
            //   184: aload_3
            //   185: astore #9
            //   187: aload #12
            //   189: astore #8
            //   191: aload #14
            //   193: iconst_1
            //   194: invokevirtual setAccessible : (Z)V
            //   197: aload_3
            //   198: astore #9
            //   200: aload #12
            //   202: astore #8
            //   204: aload #14
            //   206: aconst_null
            //   207: iconst_4
            //   208: anewarray java/lang/Object
            //   211: dup
            //   212: iconst_0
            //   213: aload #13
            //   215: aastore
            //   216: dup
            //   217: iconst_1
            //   218: aload_2
            //   219: aastore
            //   220: dup
            //   221: iconst_2
            //   222: aload_3
            //   223: aastore
            //   224: dup
            //   225: iconst_3
            //   226: new ru/androidteam/rukeyboard/ViewServer$UncloseableOuputStream
            //   229: dup
            //   230: aload_1
            //   231: invokevirtual getOutputStream : ()Ljava/io/OutputStream;
            //   234: invokespecial <init> : (Ljava/io/OutputStream;)V
            //   237: aastore
            //   238: invokevirtual invoke : (Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
            //   241: pop
            //   242: aload_3
            //   243: astore #9
            //   245: aload #12
            //   247: astore #8
            //   249: aload_1
            //   250: invokevirtual isOutputShutdown : ()Z
            //   253: ifne -> 295
            //   256: aload_3
            //   257: astore #9
            //   259: aload #12
            //   261: astore #8
            //   263: new java/io/BufferedWriter
            //   266: dup
            //   267: new java/io/OutputStreamWriter
            //   270: dup
            //   271: aload_1
            //   272: invokevirtual getOutputStream : ()Ljava/io/OutputStream;
            //   275: invokespecial <init> : (Ljava/io/OutputStream;)V
            //   278: invokespecial <init> : (Ljava/io/Writer;)V
            //   281: astore_1
            //   282: aload_1
            //   283: ldc 'DONE\\n'
            //   285: invokevirtual write : (Ljava/lang/String;)V
            //   288: aload_1
            //   289: invokevirtual flush : ()V
            //   292: aload_1
            //   293: astore #10
            //   295: iload #7
            //   297: istore #6
            //   299: aload #10
            //   301: ifnull -> 313
            //   304: aload #10
            //   306: invokevirtual close : ()V
            //   309: iload #7
            //   311: istore #6
            //   313: iload #6
            //   315: ireturn
            //   316: astore_1
            //   317: iconst_0
            //   318: istore #6
            //   320: goto -> 313
            //   323: astore #8
            //   325: aload #9
            //   327: astore_3
            //   328: aload #11
            //   330: astore_1
            //   331: aload #8
            //   333: astore #9
            //   335: aload_1
            //   336: astore #8
            //   338: ldc 'LocalViewServer'
            //   340: new java/lang/StringBuilder
            //   343: dup
            //   344: invokespecial <init> : ()V
            //   347: ldc 'Could not send command '
            //   349: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   352: aload_2
            //   353: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   356: ldc ' with parameters '
            //   358: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   361: aload_3
            //   362: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   365: invokevirtual toString : ()Ljava/lang/String;
            //   368: aload #9
            //   370: invokestatic w : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   373: pop
            //   374: iconst_0
            //   375: istore #7
            //   377: iload #7
            //   379: istore #6
            //   381: aload_1
            //   382: ifnull -> 313
            //   385: aload_1
            //   386: invokevirtual close : ()V
            //   389: iload #7
            //   391: istore #6
            //   393: goto -> 313
            //   396: astore_1
            //   397: iconst_0
            //   398: istore #6
            //   400: goto -> 313
            //   403: astore_1
            //   404: aload #8
            //   406: ifnull -> 414
            //   409: aload #8
            //   411: invokevirtual close : ()V
            //   414: aload_1
            //   415: athrow
            //   416: astore_2
            //   417: goto -> 414
            //   420: astore_2
            //   421: aload_1
            //   422: astore #8
            //   424: aload_2
            //   425: astore_1
            //   426: goto -> 404
            //   429: astore #9
            //   431: goto -> 335
            //   434: astore_1
            //   435: iconst_0
            //   436: ireturn
            // Exception table:
            //   from	to	target	type
            //   19	27	323	java/lang/Exception
            //   19	27	403	finally
            //   44	50	323	java/lang/Exception
            //   44	50	403	finally
            //   57	72	323	java/lang/Exception
            //   57	72	403	finally
            //   79	88	323	java/lang/Exception
            //   79	88	403	finally
            //   95	104	323	java/lang/Exception
            //   95	104	403	finally
            //   111	119	323	java/lang/Exception
            //   111	119	403	finally
            //   128	136	434	java/io/IOException
            //   151	184	323	java/lang/Exception
            //   151	184	403	finally
            //   191	197	323	java/lang/Exception
            //   191	197	403	finally
            //   204	242	323	java/lang/Exception
            //   204	242	403	finally
            //   249	256	323	java/lang/Exception
            //   249	256	403	finally
            //   263	282	323	java/lang/Exception
            //   263	282	403	finally
            //   282	292	429	java/lang/Exception
            //   282	292	420	finally
            //   304	309	316	java/io/IOException
            //   338	374	403	finally
            //   385	389	396	java/io/IOException
            //   409	414	416	java/io/IOException
        }

        private boolean windowManagerAutolistLoop() {
            // Byte code:
            //   0: aload_0
            //   1: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   4: aload_0
            //   5: invokestatic access$700 : (Lru/androidteam/rukeyboard/ViewServer;Lru/androidteam/rukeyboard/ViewServer$WindowListener;)V
            //   8: aconst_null
            //   9: astore_3
            //   10: aconst_null
            //   11: astore #6
            //   13: new java/io/BufferedWriter
            //   16: dup
            //   17: new java/io/OutputStreamWriter
            //   20: dup
            //   21: aload_0
            //   22: getfield mClient : Ljava/net/Socket;
            //   25: invokevirtual getOutputStream : ()Ljava/io/OutputStream;
            //   28: invokespecial <init> : (Ljava/io/OutputStream;)V
            //   31: invokespecial <init> : (Ljava/io/Writer;)V
            //   34: astore #4
            //   36: invokestatic interrupted : ()Z
            //   39: ifne -> 208
            //   42: iconst_0
            //   43: istore_1
            //   44: iconst_0
            //   45: istore_2
            //   46: aload_0
            //   47: getfield mLock : [Ljava/lang/Object;
            //   50: astore_3
            //   51: aload_3
            //   52: monitorenter
            //   53: aload_0
            //   54: getfield mNeedWindowListUpdate : Z
            //   57: ifne -> 120
            //   60: aload_0
            //   61: getfield mNeedFocusedWindowUpdate : Z
            //   64: ifne -> 120
            //   67: aload_0
            //   68: getfield mLock : [Ljava/lang/Object;
            //   71: invokevirtual wait : ()V
            //   74: goto -> 53
            //   77: astore #5
            //   79: aload_3
            //   80: monitorexit
            //   81: aload #5
            //   83: athrow
            //   84: astore #5
            //   86: aload #4
            //   88: astore_3
            //   89: ldc 'LocalViewServer'
            //   91: ldc_w 'Connection error: '
            //   94: aload #5
            //   96: invokestatic w : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   99: pop
            //   100: aload #4
            //   102: ifnull -> 110
            //   105: aload #4
            //   107: invokevirtual close : ()V
            //   110: aload_0
            //   111: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   114: aload_0
            //   115: invokestatic access$800 : (Lru/androidteam/rukeyboard/ViewServer;Lru/androidteam/rukeyboard/ViewServer$WindowListener;)V
            //   118: iconst_1
            //   119: ireturn
            //   120: aload_0
            //   121: getfield mNeedWindowListUpdate : Z
            //   124: ifeq -> 134
            //   127: aload_0
            //   128: iconst_0
            //   129: putfield mNeedWindowListUpdate : Z
            //   132: iconst_1
            //   133: istore_1
            //   134: aload_0
            //   135: getfield mNeedFocusedWindowUpdate : Z
            //   138: ifeq -> 148
            //   141: aload_0
            //   142: iconst_0
            //   143: putfield mNeedFocusedWindowUpdate : Z
            //   146: iconst_1
            //   147: istore_2
            //   148: aload_3
            //   149: monitorexit
            //   150: iload_1
            //   151: ifeq -> 167
            //   154: aload #4
            //   156: ldc_w 'LIST UPDATE\\n'
            //   159: invokevirtual write : (Ljava/lang/String;)V
            //   162: aload #4
            //   164: invokevirtual flush : ()V
            //   167: iload_2
            //   168: ifeq -> 36
            //   171: aload #4
            //   173: ldc_w 'FOCUS UPDATE\\n'
            //   176: invokevirtual write : (Ljava/lang/String;)V
            //   179: aload #4
            //   181: invokevirtual flush : ()V
            //   184: goto -> 36
            //   187: astore_3
            //   188: aload #4
            //   190: ifnull -> 198
            //   193: aload #4
            //   195: invokevirtual close : ()V
            //   198: aload_0
            //   199: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   202: aload_0
            //   203: invokestatic access$800 : (Lru/androidteam/rukeyboard/ViewServer;Lru/androidteam/rukeyboard/ViewServer$WindowListener;)V
            //   206: aload_3
            //   207: athrow
            //   208: aload #4
            //   210: ifnull -> 218
            //   213: aload #4
            //   215: invokevirtual close : ()V
            //   218: aload_0
            //   219: getfield this$0 : Lru/androidteam/rukeyboard/ViewServer;
            //   222: aload_0
            //   223: invokestatic access$800 : (Lru/androidteam/rukeyboard/ViewServer;Lru/androidteam/rukeyboard/ViewServer$WindowListener;)V
            //   226: goto -> 118
            //   229: astore_3
            //   230: goto -> 218
            //   233: astore_3
            //   234: goto -> 110
            //   237: astore #4
            //   239: goto -> 198
            //   242: astore #5
            //   244: aload_3
            //   245: astore #4
            //   247: aload #5
            //   249: astore_3
            //   250: goto -> 188
            //   253: astore #5
            //   255: aload #6
            //   257: astore #4
            //   259: goto -> 86
            // Exception table:
            //   from	to	target	type
            //   13	36	253	java/lang/Exception
            //   13	36	242	finally
            //   36	42	84	java/lang/Exception
            //   36	42	187	finally
            //   46	53	84	java/lang/Exception
            //   46	53	187	finally
            //   53	74	77	finally
            //   79	81	77	finally
            //   81	84	84	java/lang/Exception
            //   81	84	187	finally
            //   89	100	242	finally
            //   105	110	233	java/io/IOException
            //   120	132	77	finally
            //   134	146	77	finally
            //   148	150	77	finally
            //   154	167	84	java/lang/Exception
            //   154	167	187	finally
            //   171	184	84	java/lang/Exception
            //   171	184	187	finally
            //   193	198	237	java/io/IOException
            //   213	218	229	java/io/IOException
        }

        public void focusChanged() {
            synchronized (this.mLock) {
                this.mNeedFocusedWindowUpdate = true;
                this.mLock.notifyAll();
                return;
            }
        }

        public void run() {
            // Byte code:
            //   0: aconst_null
            //   1: astore_3
            //   2: aconst_null
            //   3: astore #5
            //   5: new java/io/BufferedReader
            //   8: dup
            //   9: new java/io/InputStreamReader
            //   12: dup
            //   13: aload_0
            //   14: getfield mClient : Ljava/net/Socket;
            //   17: invokevirtual getInputStream : ()Ljava/io/InputStream;
            //   20: invokespecial <init> : (Ljava/io/InputStream;)V
            //   23: sipush #1024
            //   26: invokespecial <init> : (Ljava/io/Reader;I)V
            //   29: astore #4
            //   31: aload #4
            //   33: invokevirtual readLine : ()Ljava/lang/String;
            //   36: astore #5
            //   38: aload #5
            //   40: bipush #32
            //   42: invokevirtual indexOf : (I)I
            //   45: istore_1
            //   46: iload_1
            //   47: iconst_m1
            //   48: if_icmpne -> 134
            //   51: aload #5
            //   53: astore_3
            //   54: ldc ''
            //   56: astore #5
            //   58: ldc_w 'PROTOCOL'
            //   61: aload_3
            //   62: invokevirtual equalsIgnoreCase : (Ljava/lang/String;)Z
            //   65: ifeq -> 155
            //   68: aload_0
            //   69: getfield mClient : Ljava/net/Socket;
            //   72: ldc_w '4'
            //   75: invokestatic access$200 : (Ljava/net/Socket;Ljava/lang/String;)Z
            //   78: istore_2
            //   79: iload_2
            //   80: ifne -> 109
            //   83: ldc 'LocalViewServer'
            //   85: new java/lang/StringBuilder
            //   88: dup
            //   89: invokespecial <init> : ()V
            //   92: ldc_w 'An error occurred with the command: '
            //   95: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   98: aload_3
            //   99: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   102: invokevirtual toString : ()Ljava/lang/String;
            //   105: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
            //   108: pop
            //   109: aload #4
            //   111: ifnull -> 119
            //   114: aload #4
            //   116: invokevirtual close : ()V
            //   119: aload_0
            //   120: getfield mClient : Ljava/net/Socket;
            //   123: ifnull -> 391
            //   126: aload_0
            //   127: getfield mClient : Ljava/net/Socket;
            //   130: invokevirtual close : ()V
            //   133: return
            //   134: aload #5
            //   136: iconst_0
            //   137: iload_1
            //   138: invokevirtual substring : (II)Ljava/lang/String;
            //   141: astore_3
            //   142: aload #5
            //   144: iload_1
            //   145: iconst_1
            //   146: iadd
            //   147: invokevirtual substring : (I)Ljava/lang/String;
            //   150: astore #5
            //   152: goto -> 58
            //   155: ldc_w 'SERVER'
            //   158: aload_3
            //   159: invokevirtual equalsIgnoreCase : (Ljava/lang/String;)Z
            //   162: ifeq -> 179
            //   165: aload_0
            //   166: getfield mClient : Ljava/net/Socket;
            //   169: ldc_w '4'
            //   172: invokestatic access$200 : (Ljava/net/Socket;Ljava/lang/String;)Z
            //   175: istore_2
            //   176: goto -> 79
            //   179: ldc_w 'LIST'
            //   182: aload_3
            //   183: invokevirtual equalsIgnoreCase : (Ljava/lang/String;)Z
            //   186: ifeq -> 201
            //   189: aload_0
            //   190: aload_0
            //   191: getfield mClient : Ljava/net/Socket;
            //   194: invokespecial listWindows : (Ljava/net/Socket;)Z
            //   197: istore_2
            //   198: goto -> 79
            //   201: ldc_w 'GET_FOCUS'
            //   204: aload_3
            //   205: invokevirtual equalsIgnoreCase : (Ljava/lang/String;)Z
            //   208: ifeq -> 223
            //   211: aload_0
            //   212: aload_0
            //   213: getfield mClient : Ljava/net/Socket;
            //   216: invokespecial getFocusedWindow : (Ljava/net/Socket;)Z
            //   219: istore_2
            //   220: goto -> 79
            //   223: ldc_w 'AUTOLIST'
            //   226: aload_3
            //   227: invokevirtual equalsIgnoreCase : (Ljava/lang/String;)Z
            //   230: ifeq -> 241
            //   233: aload_0
            //   234: invokespecial windowManagerAutolistLoop : ()Z
            //   237: istore_2
            //   238: goto -> 79
            //   241: aload_0
            //   242: aload_0
            //   243: getfield mClient : Ljava/net/Socket;
            //   246: aload_3
            //   247: aload #5
            //   249: invokespecial windowCommand : (Ljava/net/Socket;Ljava/lang/String;Ljava/lang/String;)Z
            //   252: istore_2
            //   253: goto -> 79
            //   256: astore_3
            //   257: aload_3
            //   258: invokevirtual printStackTrace : ()V
            //   261: goto -> 119
            //   264: astore_3
            //   265: aload_3
            //   266: invokevirtual printStackTrace : ()V
            //   269: return
            //   270: astore_3
            //   271: aload #5
            //   273: astore #4
            //   275: aload_3
            //   276: astore #5
            //   278: aload #4
            //   280: astore_3
            //   281: ldc 'LocalViewServer'
            //   283: ldc_w 'Connection error: '
            //   286: aload #5
            //   288: invokestatic w : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   291: pop
            //   292: aload #4
            //   294: ifnull -> 302
            //   297: aload #4
            //   299: invokevirtual close : ()V
            //   302: aload_0
            //   303: getfield mClient : Ljava/net/Socket;
            //   306: ifnull -> 133
            //   309: aload_0
            //   310: getfield mClient : Ljava/net/Socket;
            //   313: invokevirtual close : ()V
            //   316: return
            //   317: astore_3
            //   318: aload_3
            //   319: invokevirtual printStackTrace : ()V
            //   322: return
            //   323: astore_3
            //   324: aload_3
            //   325: invokevirtual printStackTrace : ()V
            //   328: goto -> 302
            //   331: astore #4
            //   333: aload_3
            //   334: ifnull -> 341
            //   337: aload_3
            //   338: invokevirtual close : ()V
            //   341: aload_0
            //   342: getfield mClient : Ljava/net/Socket;
            //   345: ifnull -> 355
            //   348: aload_0
            //   349: getfield mClient : Ljava/net/Socket;
            //   352: invokevirtual close : ()V
            //   355: aload #4
            //   357: athrow
            //   358: astore_3
            //   359: aload_3
            //   360: invokevirtual printStackTrace : ()V
            //   363: goto -> 341
            //   366: astore_3
            //   367: aload_3
            //   368: invokevirtual printStackTrace : ()V
            //   371: goto -> 355
            //   374: astore #5
            //   376: aload #4
            //   378: astore_3
            //   379: aload #5
            //   381: astore #4
            //   383: goto -> 333
            //   386: astore #5
            //   388: goto -> 278
            //   391: return
            // Exception table:
            //   from	to	target	type
            //   5	31	270	java/io/IOException
            //   5	31	331	finally
            //   31	46	386	java/io/IOException
            //   31	46	374	finally
            //   58	79	386	java/io/IOException
            //   58	79	374	finally
            //   83	109	386	java/io/IOException
            //   83	109	374	finally
            //   114	119	256	java/io/IOException
            //   126	133	264	java/io/IOException
            //   134	152	386	java/io/IOException
            //   134	152	374	finally
            //   155	176	386	java/io/IOException
            //   155	176	374	finally
            //   179	198	386	java/io/IOException
            //   179	198	374	finally
            //   201	220	386	java/io/IOException
            //   201	220	374	finally
            //   223	238	386	java/io/IOException
            //   223	238	374	finally
            //   241	253	386	java/io/IOException
            //   241	253	374	finally
            //   281	292	331	finally
            //   297	302	323	java/io/IOException
            //   309	316	317	java/io/IOException
            //   337	341	358	java/io/IOException
            //   348	355	366	java/io/IOException
        }

        public void windowsChanged() {
            synchronized (this.mLock) {
                this.mNeedWindowListUpdate = true;
                this.mLock.notifyAll();
                return;
            }
        }
    }

    private static interface WindowListener {
        void focusChanged();

        void windowsChanged();
    }
}
