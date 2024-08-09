package org.MovilityApp.GUI;

import org.MovilityApp.Controller.MovilityAppController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RouteSelectorGUI extends JFrame{
    private JTextField in;
    private JPanel routeSelectorPanel;
    private JTextField out;
    private JButton searchBtn;
    private JTextArea shortestPathTextArea;
    private JLabel inLabel;
    private JLabel outLabel;
    private JButton reselectFileBtn;

    public RouteSelectorGUI(MovilityAppController movilityAppController){
        setContentPane(routeSelectorPanel);
        setTitle("Buscar ruta");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (in.getText().trim().isEmpty() || out.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ingrese el paradero de salida y destino.");
                    return;
                }
                shortestPathTextArea.setText(movilityAppController.obtainRoute(in.getText(), out.getText()));
            }
        });
        reselectFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainGUI mainGUI = new MainGUI();
                mainGUI.setVisible(true);
                dispose();
            }
        });
    }
}
