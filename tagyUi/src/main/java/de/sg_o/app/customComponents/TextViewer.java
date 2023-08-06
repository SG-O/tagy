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

package de.sg_o.app.customComponents;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.extensions.rsyntaxarea.DarklafRSyntaxTheme;
import com.github.weisj.darklaf.theme.event.ThemeInstalledListener;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.app.annotator.Input;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.*;
import java.io.*;
import java.util.List;

public class TextViewer extends RTextScrollPane {
    private final RSyntaxTextArea textArea;

    @SuppressWarnings("unused")
    public TextViewer(List<Input> inputs) {
        textArea = new RSyntaxTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        Font roboto = FontImport.robotoMono;
        if (roboto != null) {
            textArea.setFont(roboto.deriveFont(16f));
        }
        DarklafRSyntaxTheme syntaxTheme = new DarklafRSyntaxTheme();
        syntaxTheme.apply(textArea);
        LafManager.addThemeChangeListener((ThemeInstalledListener) e -> syntaxTheme.apply(textArea));

        setViewportView(textArea);

        setLineNumbersEnabled(true);
        revalidate();
        repaint();
    }

    public void display(FileInfo fileInfo) {
        try (InputStream fileInfoInputStream = fileInfo.getInputStream();
             InputStreamReader fileInfoReader = new InputStreamReader(fileInfoInputStream);
             BufferedReader reader = new BufferedReader(fileInfoReader)) {
            textArea.read(reader, null);
            textArea.setCaretPosition(0);
            textArea.discardAllEdits();
        } catch (IOException ignore) {
        }
    }

    public void stop() {
        textArea.setText("");
    }

    public void close() {
        textArea.setText("");
    }
}
