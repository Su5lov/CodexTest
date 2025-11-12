package com.example.tetris;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Simple procedural audio engine for background music and sound effects.
 */
public final class ProceduralAudio {
    private static final float SAMPLE_RATE = 44100f;
    private static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);

    private Thread musicThread;
    private volatile boolean musicRunning;
    private volatile boolean musicPaused;

    public void startMusic() {
        if (musicThread != null && musicThread.isAlive() && musicRunning) {
            musicPaused = false;
            return;
        }

        musicRunning = true;
        musicPaused = false;
        musicThread = new Thread(this::playMusicLoop, "TetrisMusicThread");
        musicThread.setDaemon(true);
        musicThread.start();
    }

    public void pauseMusic() {
        musicPaused = true;
    }

    public void resumeMusic() {
        musicPaused = false;
    }

    public void stopMusic() {
        musicRunning = false;
        musicPaused = false;
        if (musicThread != null) {
            musicThread.interrupt();
            if (Thread.currentThread() != musicThread) {
                try {
                    musicThread.join(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void playLockSound() {
        playEffect(new float[]{196f}, 80, 0.6f);
    }

    public void playLineClearSound(int lines) {
        float base = lines == 4 ? 523.25f : 329.63f;
        float[] freqs = lines == 4
            ? new float[]{base, base * 1.25f, base * 1.5f}
            : new float[]{base, base * 1.5f};
        playEffect(freqs, 180, 0.5f);
    }

    private void playMusicLoop() {
        float[] sequence = {
            261.63f, 329.63f, 392.00f, 523.25f,
            392.00f, 329.63f, 293.66f, 349.23f
        };

        try (SourceDataLine line = AudioSystem.getSourceDataLine(FORMAT)) {
            line.open(FORMAT, 4096);
            line.start();

            int index = 0;
            while (musicRunning) {
                if (musicPaused) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    continue;
                }

                float freq = sequence[index % sequence.length];
                writeTone(line, freq, 220, 0.25f);
                writeSilence(line, 20);
                index++;
            }
        } catch (LineUnavailableException | IllegalArgumentException e) {
            musicRunning = false;
        }
    }

    private void playEffect(float[] freqs, int durationMs, float volume) {
        Thread thread = new Thread(() -> {
            try (SourceDataLine line = AudioSystem.getSourceDataLine(FORMAT)) {
                line.open(FORMAT, 2048);
                line.start();
                for (float freq : freqs) {
                    writeTone(line, freq, durationMs, volume);
                }
                line.drain();
            } catch (LineUnavailableException | IllegalArgumentException e) {
                // Audio not available; ignore effect.
            }
        }, "TetrisEffectSound");
        thread.setDaemon(true);
        thread.start();
    }

    private void writeTone(SourceDataLine line, float freq, int durationMs, float volume) {
        int samples = (int) ((durationMs / 1000f) * SAMPLE_RATE);
        byte[] buffer = new byte[samples];
        for (int i = 0; i < samples; ++i) {
            double angle = 2.0 * Math.PI * i * freq / SAMPLE_RATE;
            buffer[i] = (byte) (Math.sin(angle) * 127 * volume);
        }
        line.write(buffer, 0, buffer.length);
    }

    private void writeSilence(SourceDataLine line, int durationMs) {
        int samples = (int) ((durationMs / 1000f) * SAMPLE_RATE);
        byte[] buffer = new byte[samples];
        line.write(buffer, 0, buffer.length);
    }
}
