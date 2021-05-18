package com.madai.mock.utils;

import com.madai.mock.rule.SimpleMappingRules;
import org.mockserver.mock.action.ExpectationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileMonitor {



    private static final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    public static void init() throws IOException {
        WatchService ws = FileSystems.getDefault().newWatchService();
        Path p = Paths.get(Config.getConfigPath());
        p.register(ws, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        singleThreadPool.execute(new DirListner(ws));
    }

}

class DirListner implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileMonitor.class);

    private WatchService service;

    public DirListner(WatchService service) {
        this.service = service;
    }

    public void run() {
        try {
            while (true) {
                WatchKey watchKey = service.take();
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                boolean needReload = false;
                for (WatchEvent<?> event : watchEvents) {
                    String fileName = event.context().toString();
                    if (needReload(fileName)) {
                        System.out.println(event.kind().name() + "|" + fileName);
                        LOGGER.info(event.kind().name() + "|" + fileName);
                        needReload = true;
                    }
                }
                if (needReload) {
                    reload();
                }
                watchKey.reset();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("FileMonitor end");
            LOGGER.info("FileMonitor end");
            try {
                service.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void reload() {
        try {
            Config.resetDataConfig();
            SimpleMappingRules.resetRules();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean needReload(String fileName) {
        if (fileName == null) {
            return false;
        }
        if (fileName.endsWith(".json")) {
            return true;
        }
        return false;
    }
}
