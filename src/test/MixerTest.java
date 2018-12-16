package test;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import java.util.ArrayList;
import java.util.List;

public class MixerTest {

    public static void main(String[] args) {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        for (Mixer.Info mixerInfo : mixers) {
            System.out.println("Found Mixer: " + mixerInfo);
            Mixer mix = AudioSystem.getMixer(mixerInfo);

            Line.Info[] sourceInfo = mix.getSourceLineInfo();
            System.out.println("\tSource Info: ");
            for (Line.Info li : sourceInfo) {
                System.out.print("\t\tFound source info: ");
                try {
                    mix.open();
                    System.out.println(li);
                } catch (LineUnavailableException lue) {
                    System.out.println("Unavailable");
                }
            }

            Line.Info[] targetInfo = mix.getTargetLineInfo();
            System.out.println("\tTarget Info: ");
            for (Line.Info li : targetInfo) {
                System.out.print("\t\tFound target info: ");
                try {
                    mix.open();
                    System.out.println(li);
                } catch (LineUnavailableException lue) {
                    System.out.println("Unavailable");
                }
            }

            Line[] sourceLines = mix.getSourceLines();
            System.out.println("\tSources: ");
            for (Line l : sourceLines) {
                System.out.print("\t\tFound source line: ");
                try {
                    mix.open();
                    System.out.println(l);
                } catch (LineUnavailableException lue) {
                    System.out.println("Unavailable");
                }
            }

            Line[] targetLines = mix.getTargetLines();
            System.out.println("\tTargets: ");
            for (Line l : targetLines) {
                System.out.print("\t\tFound target line: ");
                try {
                    mix.open();
                    System.out.println(l);
                } catch (LineUnavailableException lue) {
                    System.out.println("Unavailable");
                }
            }
        }
    }
}
