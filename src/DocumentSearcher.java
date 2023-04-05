import javax.swing.*;
import javax.swing.text.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DocumentSearcher {
    private JTextComponent textComponent;
    List<Integer> matchStartIndexes;
    String searchText;
    boolean useRegex;

    public DocumentSearcher(JTextComponent textComponent, String searchText, boolean useRegex) {
        this.textComponent = textComponent;
        this.searchText = searchText;
        this.useRegex = useRegex;
        this.matchStartIndexes = searchText.trim().isEmpty() ? new ArrayList<>() : findMatches();
    }


    private List<Integer> findMatches() {
        List<Integer> matchIndexes = new ArrayList<>();
        String text = textComponent.getText().toLowerCase();
        String searchPattern = useRegex ? searchText : Pattern.quote(searchText.toLowerCase());

        try {
            Pattern pattern = Pattern.compile(searchPattern);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                matchIndexes.add(matcher.start());
            }
        } catch (PatternSyntaxException e) {
            System.err.println("Invalid regular expression: " + e.getMessage());
        }

        return matchIndexes;
    }

    public void highlightMatches(Highlighter.HighlightPainter highlightPainter) {
        try {
            Highlighter highlighter = textComponent.getHighlighter();
            highlighter.removeAllHighlights();
            for (int startIndex : matchStartIndexes) {
                int endIndex = startIndex + searchText.length();
                highlighter.addHighlight(startIndex, endIndex, highlightPainter);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public int selectNextMatch(int currentIndex) {
        if (matchStartIndexes.isEmpty()) {
            return currentIndex;
        }

        int nextIndex = (currentIndex + 1) % matchStartIndexes.size();
        int selectionStart = matchStartIndexes.get(nextIndex);
        int selectionEnd = useRegex ? findSelectionEnd(selectionStart) : selectionStart + searchText.length();
        textComponent.setCaretPosition(selectionStart);
        textComponent.moveCaretPosition(selectionEnd);

        return nextIndex;
    }

    public int selectPreviousMatch(int currentIndex) {
        if (matchStartIndexes.isEmpty()) {
            return currentIndex;
        }

        int previousIndex = (currentIndex - 1 + matchStartIndexes.size()) % matchStartIndexes.size();
        int selectionStart = matchStartIndexes.get(previousIndex);
        int selectionEnd = useRegex ? findSelectionEnd(selectionStart) : selectionStart + searchText.length();
        textComponent.setCaretPosition(selectionStart);
        textComponent.moveCaretPosition(selectionEnd);

        return previousIndex;
    }


    int findSelectionEnd(int selectionStart) {
        if (searchText.trim().isEmpty()) {
            return selectionStart;
        }
        String text = textComponent.getText();
        Pattern pattern = Pattern.compile(searchText);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find(selectionStart)) {
            return matcher.end();
        }
        return selectionStart;
    }


}

