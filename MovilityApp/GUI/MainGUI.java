package org.MovilityApp.GUI;

import org.MovilityApp.Controller.MovilityAppController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainGUI extends JFrame{
    private JButton seleccionarArchivoButton;
    private JPanel mainPanel;

    public MainGUI() {
        setContentPane(mainPanel);
        setTitle("Seleccionar archivo");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        seleccionarArchivoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Seleccionar Archivo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300, 200);

                JFileChooser fileChooser = new JFileChooser();

                int returnValue = fileChooser.showOpenDialog(frame);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (!selectedFile.getAbsolutePath().endsWith(".txt")) {
                        JOptionPane.showMessageDialog(null,"El archivo debe ser de formato .txt.","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    seleccionarArchivoButton.setEnabled(false);
                    RouteSelectorGUI routeSelectorGUI = new RouteSelectorGUI(new MovilityAppController(selectedFile.getAbsolutePath()));
                    routeSelectorGUI.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null,"No se seleccionó ningún archivo.","Error",JOptionPane.INFORMATION_MESSAGE);
                }

                frame.dispose();
            }
        });
    }
}
