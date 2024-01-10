package su.nightexpress.ama.api.arena;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Colorizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static su.nexmedia.engine.utils.Colors2.*;
import static su.nexmedia.engine.utils.Colors2.GRAY;

public class Report {

    private static final String PREFIX_PROBLEM = Colorizer.apply(RED + "✘ " + GRAY);
    private static final String PREFIX_GOOD = Colorizer.apply(GREEN + "✔ " + GRAY);
    private static final String PREFIX_WARN = Colorizer.apply(ORANGE + "❗ " + GRAY);

    public enum Type {
        PROBLEM, WARN, GOOD
    }

    private final Map<Type, List<String>> reportMap;

    public Report() {
        this.reportMap = new HashMap<>();
    }

    public boolean hasProblems() {
        return !this.getProblems().isEmpty();
    }

    public boolean hasWarns() {
        return !this.getWarns().isEmpty();
    }

    @NotNull
    public List<String> getFullReport() {
        List<String> report = new ArrayList<>();
        report.addAll(this.getProblems());
        report.addAll(this.getWarns());
        report.addAll(this.getGoods());
        return report;
    }

    @NotNull
    public Report addProblem(@NotNull String text) {
        return this.addReport(Type.PROBLEM, text);
    }

    @NotNull
    public Report addWarn(@NotNull String text) {
        return this.addReport(Type.WARN, text);
    }

    @NotNull
    public Report addGood(@NotNull String text) {
        return this.addReport(Type.GOOD, text);
    }

    @NotNull
    public Report addReport(@NotNull Type type, @NotNull String text) {
        this.getReports(type).add(text);
        return this;
    }

    @NotNull
    public List<String> getProblems() {
        return this.getReports(Type.PROBLEM).stream().map(Report::problem).toList();
    }

    @NotNull
    public List<String> getWarns() {
        return this.getReports(Type.WARN).stream().map(Report::warn).toList();
    }

    @NotNull
    public List<String> getGoods() {
        return this.getReports(Type.GOOD).stream().map(Report::good).toList();
    }

    @NotNull
    public List<String> getReports(@NotNull Type type) {
        return this.reportMap.computeIfAbsent(type, k -> new ArrayList<>());
    }

    @NotNull
    public static String problem(@NotNull String text) {
        return PREFIX_PROBLEM + text;
    }

    @NotNull
    public static String good(@NotNull String text) {
        return PREFIX_GOOD + text;
    }

    @NotNull
    public static String warn(@NotNull String text) {
        return PREFIX_WARN + text;
    }
}
