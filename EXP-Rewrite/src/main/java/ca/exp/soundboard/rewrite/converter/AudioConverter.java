package ca.exp.soundboard.rewrite.converter;

import java.io.File;
import javax.swing.JOptionPane;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncoderProgressListener;
import it.sauronsoftware.jave.EncodingAttributes;

public class AudioConverter {

    private static final int MP3_BITRATE = 256000;
    private static final int CHANNELS = 2;
    private static final int SAMPLE_RATE = 44100;
    private static final int LISTENER_PERMIL_PROGRESS = 1001;

    private static final char NEWLINE_CHARACTER = '\n';
    private static final char PERIOD_CHARACTER = '.';

    private static final String INCOMPATIBLE_INPUT_FILE_MESSAGE_TILE = "Input File incompatible";
    private static final String INCOMPATIBLE_INPUT_FILE_MESSAGE = "Input file formatting/encoding is incompatible";

    private static final String MP3_FILE_EXTENSION = ".mp3";
    private static final String WAV_FILE_EXTENSION = ".wav";

    private static final String AUDIO_ATTRIBUTES_MP3_CODEC = "libmp3lame";
    private static final String MP3_ENCODING_ATTRIBUTES_FORMAT = "mp3";

    private static final String AUDIO_ATTRIBUTES_WAV_CODEC = "pcm_s16le";
    private static final String WAV_ENCODING_ATTRIBUTES_FORMAT = "wav";

    public static void batchConvertToMP3(File[] inputFiles, final File outputFolder,
            final EncoderProgressListener listener) {
        new Thread(new Runnable() {
            public void run() {
                for (File input : inputFiles) {
                    File output = AudioConverter.getAbsoluteForOutputExtensionAndFolder(input, outputFolder, MP3_FILE_EXTENSION);
                    AudioConverter.mp3(input, output, listener);
                }
            }
        }).start();
    }

    public static void batchConvertToWAV(File[] inputFiles, final File outputFolder,
            final EncoderProgressListener listener) {
        new Thread(new Runnable() {
            public void run() {
                for (File input : inputFiles) {
                    File output = AudioConverter.getAbsoluteForOutputExtensionAndFolder(input, outputFolder, WAV_FILE_EXTENSION);
                    AudioConverter.wav(input, output, listener);
                }
            }
        }).start();
    }

    public static void convertToMP3(File inputFile, final File outputFile, final EncoderProgressListener listener) {
        new Thread(new Runnable() {
            public void run() {
                AudioConverter.mp3(inputFile, outputFile, listener);
            }
        }).start();
    }

    public static void convertToWAV(File inputFile, final File outputFile, final EncoderProgressListener listener) {
        new Thread(new Runnable() {
            public void run() {
                AudioConverter.wav(inputFile, outputFile, listener);
            }
        }).start();
    }

    private static void mp3(File inputFile, File outputFile, EncoderProgressListener listener) {
        AudioAttributes audioAttributes = new AudioAttributes();
        audioAttributes.setCodec(AUDIO_ATTRIBUTES_MP3_CODEC);
        audioAttributes.setBitRate(MP3_BITRATE);
        audioAttributes.setChannels(CHANNELS);
        audioAttributes.setSamplingRate(SAMPLE_RATE);

        EncodingAttributes encodingAttributes = new EncodingAttributes();
        encodingAttributes.setFormat(MP3_ENCODING_ATTRIBUTES_FORMAT);
        encodingAttributes.setAudioAttributes(audioAttributes);

        Encoder encoder = new Encoder();

        try {
            if (listener != null) {
                encoder.encode(inputFile, outputFile, encodingAttributes, listener);
            } else {
                encoder.encode(inputFile, outputFile, encodingAttributes);
            }
        } catch (IllegalArgumentException | EncoderException exception) {
            JOptionPane.showMessageDialog(
                    null,
                    INCOMPATIBLE_INPUT_FILE_MESSAGE + NEWLINE_CHARACTER + inputFile.getName(), INCOMPATIBLE_INPUT_FILE_MESSAGE_TILE,
                    0);

            listener.progress(LISTENER_PERMIL_PROGRESS);
            exception.printStackTrace();
        }
    }

    private static void wav(File inputFile, File outputFile, EncoderProgressListener listener) {
        AudioAttributes audioAttributes = new AudioAttributes();
        audioAttributes.setCodec(AUDIO_ATTRIBUTES_WAV_CODEC);

        EncodingAttributes encodingAttributes = new EncodingAttributes();
        encodingAttributes.setFormat(WAV_ENCODING_ATTRIBUTES_FORMAT);
        encodingAttributes.setAudioAttributes(audioAttributes);

        Encoder encoder = new Encoder();

        try {
            if (listener != null) {
                encoder.encode(inputFile, outputFile, encodingAttributes, listener);
            } else {
                encoder.encode(inputFile, outputFile, encodingAttributes);
            }
        } catch (IllegalArgumentException | EncoderException exception) {
            JOptionPane.showMessageDialog(
                    null,
                    INCOMPATIBLE_INPUT_FILE_MESSAGE + NEWLINE_CHARACTER + inputFile.getName(), INCOMPATIBLE_INPUT_FILE_MESSAGE_TILE,
                    0);

            listener.progress(LISTENER_PERMIL_PROGRESS);
            exception.printStackTrace();
        }
    }

    private static File getAbsoluteForOutputExtensionAndFolder(File inputFile, File outputFolder, String dotExtension) {
        String fileName = inputFile.getName();
        int period = fileName.lastIndexOf(PERIOD_CHARACTER);

        if (period > 0) {
            fileName = fileName.substring(0, period) + dotExtension;
        }

        return new File(outputFolder + File.separator + fileName);
    }
}
