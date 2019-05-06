package cn.modificator.launcher.util;

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class LooperExecutor extends AbstractExecutorService {

    private final Handler mHandler;

    public LooperExecutor(Looper looper) {
        mHandler = new Handler(looper);
    }

    public Handler getmHandler() {
        return mHandler;
    }


    @Override
    public void execute(Runnable command) {
        if (mHandler.getLooper() == Looper.myLooper()) {
            command.run();
        } else {
            mHandler.post(command);
        }
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }
}
