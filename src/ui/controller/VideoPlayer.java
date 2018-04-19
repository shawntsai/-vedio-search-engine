package ui.controller;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class VideoPlayer {
    private final static int FRAMERATE = 30;
    public Thread imageThread;
    public int currentFrame;
    private PlayerStatus status;
    private int maxFrameNum;
    private JLabel dispalyArea;
    private BufferedImage[] sourceFrames;

    public VideoPlayer(int maxFrameNum, JLabel area, BufferedImage[] sourceFrames) {
        this.currentFrame = 1;
        this.maxFrameNum = maxFrameNum;
        this.dispalyArea = area;
        this.sourceFrames = sourceFrames;
    }

    public void play() {
        status = PlayerStatus.Play;
        imageThread = new Thread(() -> {
            for(int cur = this.currentFrame; cur <= this.maxFrameNum; cur++) {
                dispalyArea.setIcon(new ImageIcon(sourceFrames[cur]));
                try {
                    sleep(1000/FRAMERATE);
                }catch(InterruptedException e) {
                    if(status == PlayerStatus.Stop) {
                      this.currentFrame = 1;
                    } else {
                        this.currentFrame = cur;
                        // status = PlayerStatus.Play;
                    }
                    dispalyArea.setIcon(new ImageIcon(sourceFrames[this.currentFrame]));
                    currentThread().interrupt();
                    break;
                }
            }
            if(status == PlayerStatus.Play) {
                status = PlayerStatus.Stop;
                this.currentFrame = 1;
//                this.imageThread = null;
            }
        });
        imageThread.start();
    }

    public void pause() {
        status = PlayerStatus.Pause;
        if(imageThread != null) {
            imageThread.interrupt();
            imageThread = null;
        }
    }

    public void stop() {
        status = PlayerStatus.Stop;
        if(imageThread != null) {
            imageThread.interrupt();
            imageThread = null;
        } else {
            this.currentFrame = 1;
            displayDefaultImg();
        }
    }

    private void displayDefaultImg() {
        Thread initThread = new Thread(()->
           this.dispalyArea.setIcon(new ImageIcon(sourceFrames[this.currentFrame]))
        );
        initThread.start();
    }
}
