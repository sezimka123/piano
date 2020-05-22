package sample;


import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.Arrays;
import java.util.List;


public class Main extends Application {

    private List<Note> notes = Arrays.asList(
            new Note("до", KeyCode.Q, 60),
            new Note("ре", KeyCode.W, 62),
            new Note("ми", KeyCode.E, 64),
            new Note("фа", KeyCode.R, 65),
            new Note("соль", KeyCode.T, 67),
            new Note("ля", KeyCode.Y, 69),
            new Note("си", KeyCode.U, 71),
            new Note("до", KeyCode.I, 72),
            new Note("ре", KeyCode.O, 74),
            new Note("ми", KeyCode.P, 76)
    );

    private HBox root = new HBox(15);

    private MidiChannel channel;

    private Parent createContent() {
        loadChannel();

        root.setPrefSize(600, 150);

        notes.forEach(note -> {
            NoteView view = new NoteView(note);
            root.getChildren().addAll(view);
        });

        return root;
    }

    private void loadChannel() {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            synth.loadInstrument(synth.getDefaultSoundbank().getInstruments()[5]);

            channel = synth.getChannels()[0];

        } catch (MidiUnavailableException e) {
            System.out.println("Cannot get synth");
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(createContent());
        scene.setOnKeyPressed(e -> onKeyPress(e.getCode()));

        stage.setScene(scene);
        stage.show();
    }

    private void onKeyPress(KeyCode key) {
        root.getChildren()
                .stream()
                .map(view -> (NoteView) view)
                .filter(view -> view.note.key.equals(key))
                .forEach(view -> {

                    FillTransition ft = new FillTransition(
                            Duration.seconds(0.15),
                            view.bg,
                            Color.WHITE,
                            Color.BLACK
                    );
                    ft.setCycleCount(2);
                    ft.setAutoReverse(true);
                    ft.play();

                    channel.noteOn(view.note.number, 90);
                });
    }

    private static class NoteView extends StackPane {
        private Note note;
        private Rectangle bg = new Rectangle(50, 150, Color.WHITE);

        NoteView(Note note) {
            this.note = note;

            bg.setStroke(Color.BLACK);
            bg.setStrokeWidth(2.5);

            getChildren().addAll(bg, new Text(note.name));
        }
    }

    private static class Note {
        private String name;
        private KeyCode key;
        private int number;

        Note(String name, KeyCode key, int number) {
            this.name = name;
            this.key = key;
            this.number = number;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}