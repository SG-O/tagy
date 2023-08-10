/*
 *
 *      Copyright (C) 2023 Joerg Bayer (SG-O)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.sg_o.app.tagy.customComponents.licenseViewer;

import de.sg_o.app.tagy.customComponents.Icons;
import de.sg_o.app.tagy.customComponents.ScalableFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LicenseListPane extends JPanel {
    private static final String LIST_PANEL = "LIST_PANEL";
    private static final String DETAILS_PANEL = "DETAILS_PANEL";

    private final JPanel listPanel;

    private final JLabel detailDepName;
    private final JLabel detailLicenseName;
    private final JTextArea detailLicenseText;
    private final JScrollPane detailLicenseTextScroll;
    private final JLabel detailLicenseUrl;


    public LicenseListPane(LicenseList licenseList) {
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JScrollPane listScrollPane = new JScrollPane(listPanel);
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        JButton backButton = new JButton(Icons.ARROW_BACK_24);
        backButton.addActionListener(e -> ((CardLayout)(getLayout())).show(this, LIST_PANEL));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailDepName = new JLabel();
        detailDepName.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailLicenseName = new JLabel();
        detailLicenseName.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailLicenseText = new JTextArea();
        detailLicenseText.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailLicenseTextScroll = new JScrollPane(detailLicenseText);
        detailLicenseUrl = new JLabel();
        detailLicenseText.setEditable(false);
        detailLicenseText.setLineWrap(true);
        detailLicenseText.setWrapStyleWord(true);

        detailsPanel.add(backButton);
        detailsPanel.add(detailDepName);
        detailsPanel.add(detailLicenseName);
        detailsPanel.add(new JSeparator());
        detailsPanel.add(detailLicenseTextScroll);
        detailsPanel.add(new JSeparator());
        detailsPanel.add(detailLicenseUrl);

        setLayout(new CardLayout());

        add(listScrollPane, LIST_PANEL);
        add(detailsPanel, DETAILS_PANEL);

        for (License license : licenseList) {
            createEntry(license);
        }
    }

    private void createEntry(License license) {
        JLabel name = new JLabel(license.getDependencyName());
        name.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                showDetails(license);
            }
        });
        JLabel licenseName = new JLabel(license.getLicenseName());
        name.setFont(new ScalableFont(name.getFont()).deriveFont(20f));
        listPanel.add(name);
        listPanel.add(licenseName);
        listPanel.add(new JSeparator());
    }

    private void showDetails(License license) {
        detailDepName.setText(license.getDependencyName());
        detailLicenseName.setText(license.getLicenseName());
        detailLicenseText.setText(license.getLicenseText());
        detailLicenseText.revalidate();
        detailLicenseText.repaint();
        javax.swing.SwingUtilities.invokeLater(() -> detailLicenseTextScroll.getVerticalScrollBar().setValue(0));
        detailLicenseUrl.setText(license.getLicenseUrl());
        detailLicenseUrl.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(license.getLicenseUrl()));
                    } catch (IOException | URISyntaxException ignored) {
                    }
                }
            }
        });

        CardLayout cl = (CardLayout)(getLayout());
        cl.show(this, DETAILS_PANEL);
    }
}
