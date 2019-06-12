package org.mbari.vcr4j.vlcj;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.mbari.vcr4j.util.Preconditions;
//import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
//import uk.co.caprica.vlcj.player.direct.BufferFormat;
//import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
//import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
//import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.nio.ByteBuffer;

/**
 * @author Brian Schlining
 * @since 2016-08-16T14:16:00
 */
public class SimpleJavaFXVideoStage extends Stage {

    private final String pathToVideo;

    private ImageView imageView;

    private DirectMediaPlayerComponent mediaPlayerComponent;

    private WritableImage writableImage;

    private Pane playerHolder;

    private WritablePixelFormat<ByteBuffer> pixelFormat;

    private FloatProperty videoSourceRatioProperty;

    public SimpleJavaFXVideoStage(String pathToVideo) {
        this(pathToVideo, StageStyle.DECORATED);
    }

    public SimpleJavaFXVideoStage(String pathToVideo, StageStyle style) {
        super(style);
        Preconditions.checkArgument(pathToVideo != null, "The video path can not be null");
        this.pathToVideo = pathToVideo;
        initialize();
    }

    public ImageView getImageView() {
        return imageView;
    }

    private void initialize() {
        mediaPlayerComponent = new SimpleJavaFXVideoStage.CanvasPlayerComponent();
        playerHolder = new Pane();
        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);
        pixelFormat = PixelFormat.getByteBgraPreInstance();
        initializeImageView();
        Scene scene = new Scene(new BorderPane(playerHolder));
        scene.setFill(Color.BLACK);
        setScene(scene);
//        mediaPlayerComponent.getMediaPlayer().prepareMedia(pathToVideo);
//        mediaPlayerComponent.getMediaPlayer().start();
        //primaryStage.show();
    }

    private void initializeImageView() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        writableImage = new WritableImage((int) visualBounds.getWidth(), (int) visualBounds.getHeight());

        imageView = new ImageView(writableImage);
        playerHolder.getChildren().add(imageView);

        playerHolder.widthProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize(newValue.floatValue(), (float) playerHolder.getHeight());
        });

        playerHolder.heightProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize((float) playerHolder.getWidth(), newValue.floatValue());
        });

        videoSourceRatioProperty.addListener((observable, oldValue, newValue) -> {
            fitImageViewSize((float) playerHolder.getWidth(), (float) playerHolder.getHeight());
        });
    }

    private void fitImageViewSize(float width, float height) {
        Platform.runLater(() -> {
            float fitHeight = videoSourceRatioProperty.get() * width;
            if (fitHeight > height) {
                imageView.setFitHeight(height);
                double fitWidth = height / videoSourceRatioProperty.get();
                imageView.setFitWidth(fitWidth);
                imageView.setX((width - fitWidth) / 2);
                imageView.setY(0);
            } else {
                imageView.setFitWidth(width);
                imageView.setFitHeight(fitHeight);
                imageView.setY((height - fitHeight) / 2);
                imageView.setX(0);
            }
        });
    }

    private class CanvasPlayerComponent extends DirectMediaPlayerComponent {

        public CanvasPlayerComponent() {
            super(new SimpleJavaFXVideoStage.CanvasBufferFormatCallback());
        }

        PixelWriter pixelWriter = null;

        private PixelWriter getPixelWriter() {
            if (pixelWriter == null) {
                pixelWriter = writableImage.getPixelWriter();
            }
            return pixelWriter;
        }

        /* VLCj 3.10.0
        @Override
        public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
            if (writableImage == null) {
                return;
            }
            Platform.runLater(() -> {
                Memory nativeBuffer = mediaPlayer.lock()[0];
                try {
                    ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                    getPixelWriter().setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                }
                finally {
                    mediaPlayer.unlock();
                }
            });
        } */

        /**
         * For VLCJ experimental
         * @param mediaPlayer
         * @param nativeBuffers
         * @param bufferFormat
         */
        @Override
        public void display(DirectMediaPlayer mediaPlayer, ByteBuffer[] nativeBuffers, BufferFormat bufferFormat) {
            if (writableImage == null) {
                return;
            }
            Platform.runLater(() -> {
                ByteBuffer byteBuffer = mediaPlayer.lock()[0];
                try {
                    getPixelWriter().setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                }
                finally {
                    mediaPlayer.unlock();
                }
            });
        }
    }

    private class CanvasBufferFormatCallback implements BufferFormatCallback {
        @Override
        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            Platform.runLater(() -> videoSourceRatioProperty.set((float) sourceHeight / (float) sourceWidth));
            return new RV32BufferFormat((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
        }
    }
}
