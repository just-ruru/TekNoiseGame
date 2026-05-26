package main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeaderboardManager {

    public static class Entry {
        public final String name;
        public final long timeNanos;
        public final String completedAt;

        public Entry(String name, long timeNanos, String completedAt) {
            this.name = name;
            this.timeNanos = timeNanos;
            this.completedAt = completedAt;
        }
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Path leaderboardPath;

    public LeaderboardManager(Path leaderboardPath) {
        this.leaderboardPath = leaderboardPath;
    }

    public List<Entry> readEntries() {
        List<Entry> entries = new ArrayList<>();
        try {
            ensureParentDirectory();
            if (!Files.exists(leaderboardPath)) {
                return entries;
            }

            List<String> lines = Files.readAllLines(leaderboardPath, StandardCharsets.UTF_8);
            for (String line : lines) {
                Entry parsed = parseLine(line);
                if (parsed != null) {
                    entries.add(parsed);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read leaderboard: " + e.getMessage());
        }

        entries.sort(Comparator.comparingLong(entry -> entry.timeNanos));
        return entries;
    }

    public List<Entry> addEntry(String playerName, long timeNanos) {
        List<Entry> entries = readEntries();
        String safeName = sanitizeName(playerName);
        entries.add(new Entry(safeName, Math.max(0L, timeNanos), LocalDateTime.now().format(DATE_FORMATTER)));
        entries.sort(Comparator.comparingLong(entry -> entry.timeNanos));

        List<String> lines = new ArrayList<>();
        for (Entry entry : entries) {
            lines.add(serialize(entry));
        }

        try {
            ensureParentDirectory();
            Files.write(leaderboardPath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Failed to write leaderboard: " + e.getMessage());
        }

        return entries;
    }

    private void ensureParentDirectory() throws IOException {
        Path parent = leaderboardPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    private Entry parseLine(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length < 3) {
            return null;
        }

        try {
            String name = sanitizeName(parts[0]);
            long timeNanos = Long.parseLong(parts[1]);
            String completedAt = parts[2].trim();
            return new Entry(name, timeNanos, completedAt);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String serialize(Entry entry) {
        return sanitizeName(entry.name) + "|" + Math.max(0L, entry.timeNanos) + "|" + entry.completedAt;
    }

    private String sanitizeName(String rawName) {
        if (rawName == null) {
            return "Anonymous";
        }

        String cleaned = rawName.replace("|", " ").trim();
        if (cleaned.isEmpty()) {
            return "Anonymous";
        }

        return cleaned.length() > 16 ? cleaned.substring(0, 16) : cleaned;
    }
}
