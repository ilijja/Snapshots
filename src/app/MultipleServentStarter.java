package app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MultipleServentStarter {

    private static final String TEST_DIR = "/Users/ilija/Downloads/Snapshots/snapshot-primer";
    private static final String OUT_DIR = "/Users/ilija/Downloads/Snapshots/out/production/Snapshots";

    public static void main(String[] args) {
        List<Process> servents = new ArrayList<>();

        Config.load(TEST_DIR + "/servent_list.properties");

        App.print("Starting multiple servents - Type \"stop\" to exit");

        for (int i = 0; i < Config.SERVENT_COUNT; i++) {
            try {
                ProcessBuilder builder = new ProcessBuilder("java", "-cp", OUT_DIR, "app.ServentMain",
                        TEST_DIR + "/servent_list.properties", String.valueOf(i));

                builder.redirectOutput(new File(TEST_DIR + "/output/servent" + i + "_out.txt"));
                builder.redirectError(new File(TEST_DIR + "/error/servent" + i + "_err.txt"));
                builder.redirectInput(new File(TEST_DIR + "/input/servent" + i + "_in.txt"));

                servents.add(builder.start());
            } catch (IOException e) {
                App.error("Error while starting servents");
            }
        }

        ServentStarter starter = new ServentStarter(servents);
        new Thread(starter).start();

        for (Process process : servents) {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        App.print("All servents stopped");

        starter.stop();
    }

    private static class ServentStarter implements Runnable {

        private volatile boolean working = true;

        private List<Process> servents;

        private ServentStarter(List<Process> servents) {
            this.servents = servents;
        }

        @Override
        public void run() {
            try (Scanner sc = new Scanner(System.in)) {
                while (working) {
                    if (System.in.available() > 0 && sc.nextLine().equals("stop")) {
                        throw new InterruptedException("Stopping servents");
                    }
                }
            } catch (Exception e) {
                App.error(e.getMessage());
            } finally {
                for (Process process : servents) {
                    process.destroy();
                }
            }
        }

        public void stop() {
            working = false;
        }
    }
}
