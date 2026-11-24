package labyrinth;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Plays a music in a loop, in the background.
 * <p>
 * The file is streamed, not loaded in memory (the music of the game weighs more than 130 MB). If it cannot be
 * played (no file, no sound device...), the game just goes on in silence.
 */
final class MusicPlayer {
    private static final int BUFFER_SIZE = 32 * 1024;

    private MusicPlayer() {
    }

    /**
     * Starts playing a music in a loop, until the program ends.
     *
     * @param path : path of a WAV file
     */
    static void playInBackground(String path) {
        Thread thread = new Thread(() -> loop(path), "music");
        thread.setDaemon(true);
        thread.start();
    }

    private static void loop(String path) {
        try (AudioInputStream first = open(path)) {
            AudioFormat format = first.getFormat();
            try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                line.open(format);
                line.start();
                byte[] buffer = new byte[BUFFER_SIZE];
                AudioInputStream stream = first;
                boolean played;
                do {
                    played = false;
                    int read;
                    while ((read = stream.read(buffer, 0, buffer.length)) > 0) {
                        line.write(buffer, 0, read);
                        played = true;
                    }
                    if (stream != first) {
                        stream.close();
                    }
                    if (played) {
                        stream = open(path);
                    }
                } while (played);
                line.drain();
            }
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | IllegalArgumentException e) {
            // No music: not a reason to stop the game.
        }
    }

    private static AudioInputStream open(String path) throws IOException, UnsupportedAudioFileException {
        return AudioSystem.getAudioInputStream(new BufferedInputStream(Resources.open(path)));
    }
}
