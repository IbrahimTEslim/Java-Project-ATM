package banksoftware.Classes;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.FileInputStream;
import java.io.InputStream;

class PLaySound {
    public void playWithdrawSound(String path) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    InputStream in = new FileInputStream(path);

                    // create an audiostream from the inputstream
                    AudioStream audioStream = new AudioStream(in);

                    // play the audio clip with the audioplayer class
                    AudioPlayer.player.start(audioStream);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    public void playDepositSound(String path) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    InputStream in = new FileInputStream(path);

                    // create an audiostream from the inputstream
                    AudioStream audioStream = new AudioStream(in);

                    // play the audio clip with the audioplayer class
                    AudioPlayer.player.start(audioStream);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();

    }
}
