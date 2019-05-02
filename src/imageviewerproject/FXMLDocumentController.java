package imageviewerproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {

  private final List<Image> images = new ArrayList<>();
  public Slider delaySlider;
  public Label pathLabel;
  Thread t;
  private List<File> files = new ArrayList<>();
  private int currentImageIndex;
  @FXML
  private Button btnLoad;

  @FXML
  private Button btnPrevious;

  @FXML
  private Button btnNext;

  @FXML
  private ImageView imageView;

  private void handleBtnLoadAction(final ActionEvent event) {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select image files");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images",
        "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
    files = fileChooser.showOpenMultipleDialog(new Stage());

    try {
      saveFiles();
    } catch (final IOException ioe) {
      ioe.printStackTrace();
    }

    if (!files.isEmpty()) {
      files.forEach((File f) ->
          images.add(new Image(f.toURI().toString())));
      displayImage();
    }
  }


  private void changeImage(int value) {
    if (!images.isEmpty()) {
      currentImageIndex = (currentImageIndex + value) % images.size();
      displayImage();
    }
  }

  private void handleBtnPreviousAction(final ActionEvent event) {
    changeImage(-1);
  }


  private void handleBtnNextAction(final ActionEvent event) {
    changeImage(1);
  }

  private void displayImage() {
    if (!images.isEmpty()) {
      imageView.setImage(images.get(currentImageIndex));
      Platform.runLater(() -> setPathLabel());
    }
  }

  @Override
  public void initialize(final URL url, final ResourceBundle rb) {
    btnLoad.setOnAction((ActionEvent event) ->
        handleBtnLoadAction(event));

    btnPrevious.setOnAction((ActionEvent event) ->
        handleBtnPreviousAction(event));

    btnNext.setOnAction((ActionEvent event) ->
        handleBtnNextAction(event));

    try {
      loadFiles();
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final ClassNotFoundException e) {
      e.printStackTrace();
    }

    if (!files.isEmpty()) {
      files.forEach((File f) ->
          images.add(new Image(f.toURI().toString())));
      displayImage();
    }
  }

  public void loadFiles() throws IOException, ClassNotFoundException {
    final FileInputStream fileIn = new FileInputStream("files");
    final ObjectInputStream in = new ObjectInputStream(fileIn);
    final List<File> teams = (List<File>) in.readObject();
    fileIn.close();
    in.close();
    this.files = teams;
  }

  public void startShow(final ActionEvent actionEvent) {
    t = new Thread(new newShow());
    t.start();
  }


  public void stopShow(final ActionEvent actionEvent) {
    t.stop();
  }

  //path.indexOf("\\", path.indexOf("\\", path.indexOf("\\") + 1) + 1);
  private void setPathLabel() {
    final String path = files.get(this.currentImageIndex).toString();
    int firstIndex = path.indexOf("\\");
    for (int i = 0; i < 2; i++) {
      firstIndex = path.indexOf("\\", firstIndex + 1);
    }
    pathLabel.setText(path.substring(firstIndex));
  }

  public void saveFiles() throws IOException {
    final FileOutputStream fos = new FileOutputStream("files");
    final ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(files);
    oos.close();
    fos.close();
  }

  public class newShow implements Runnable {
    @Override
    public void run() {
      while (true) {
        changeImage(1);
        try {
          Thread.sleep((int) delaySlider.getValue());
        } catch (final InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

  }
}
