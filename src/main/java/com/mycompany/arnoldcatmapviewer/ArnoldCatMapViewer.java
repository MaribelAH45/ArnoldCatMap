/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.arnoldcatmapviewer;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ArnoldCatMapViewer extends JFrame {
    private final JPanel imagePanel;
    private final JButton toggleButton;
    private boolean showingForward = true;
    private BufferedImage originalImage;
    private BufferedImage scrambledImage;
    private int period;

    public ArnoldCatMapViewer() {
        setTitle("Arnold Cat Map - Interactivo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        imagePanel = new JPanel(new GridLayout(1, 10, 10, 10));
        add(imagePanel, BorderLayout.CENTER);

        toggleButton = new JButton("Mostrar Regreso");
        toggleButton.addActionListener(e -> {
            showingForward = !showingForward;
            updateImages();
        });
        add(toggleButton, BorderLayout.SOUTH);

        try {
            originalImage = ImageIO.read(new File("src/main/Resources/Imagenes/input.jpg"));
            scrambledImage = applyArnoldCatMap(originalImage, 10);
            period = findPeriod(originalImage);

            updateImages();

            setSize(1400, 300);
            setLocationRelativeTo(null);
            setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar la imagen", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateImages() {
        imagePanel.removeAll();
        if (showingForward) {
            setTitle("Iteraciones del Arnold Cat Map (Cifrado)");
            for (int i = 0; i < 10; i++) {
                BufferedImage transformed = applyArnoldCatMap(originalImage, i);
                imagePanel.add(createLabeledImage(transformed, "Iteración " + i));
            }
            toggleButton.setText("Mostrar Regreso");
        } else {
            setTitle("Regresando a la Imagen Original");
            for (int i = 0; i < 10; i++) {
                int inverseIter = period - 10 - i;
                BufferedImage reverted = applyArnoldCatMap(scrambledImage, inverseIter);
                imagePanel.add(createLabeledImage(reverted, "Regreso " + i));
            }
            toggleButton.setText("Mostrar Iteraciones");
        }
        revalidate();
        repaint();
    }

    private JLabel createLabeledImage(BufferedImage img, String label) {
        ImageIcon icon = new ImageIcon(img.getScaledInstance(128, 128, Image.SCALE_SMOOTH));
        JLabel lbl = new JLabel(icon);
        lbl.setText(label);
        lbl.setHorizontalTextPosition(JLabel.CENTER);
        lbl.setVerticalTextPosition(JLabel.BOTTOM);
        return lbl;
    }

    public static BufferedImage applyArnoldCatMap(BufferedImage img, int iterations) {
        int size = img.getWidth();
        BufferedImage current = new BufferedImage(size, size, img.getType());

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                current.setRGB(x, y, img.getRGB(x, y));
            }
        }

        for (int k = 0; k < iterations; k++) {
            BufferedImage next = new BufferedImage(size, size, img.getType());
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    int newX = (x + y) % size;
                    int newY = (x + 2 * y) % size;
                    next.setRGB(newX, newY, current.getRGB(x, y));
                }
            }
            current = next;
        }

        return current;
    }

    public static int findPeriod(BufferedImage original) {
        int size = original.getWidth();
        BufferedImage current = new BufferedImage(size, size, original.getType());

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                current.setRGB(x, y, original.getRGB(x, y));
            }
        }

        int period = 0;
        while (true) {
            period++;
            current = applyArnoldCatMap(current, 1);
            if (imagesAreEqual(current, original)) break;
            if (period > 1000) break;
        }
        return period;
    }

    public static boolean imagesAreEqual(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ArnoldCatMapViewer());
    }
}