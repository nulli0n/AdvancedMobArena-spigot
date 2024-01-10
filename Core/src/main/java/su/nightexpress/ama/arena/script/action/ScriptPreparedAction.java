package su.nightexpress.ama.arena.script.action;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.regex.RegexUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScriptPreparedAction {

    private static final String PATTERN_PARAMETER = ":(?:'|\")(.*?)(?:'|\")(?=>|\\s|$)";

    private final ScriptAction    action;
    private final ParameterResult parameterResult;

    public ScriptPreparedAction(@NotNull ScriptAction action, @NotNull ParameterResult parameterResult) {
        this.action = action;
        this.parameterResult = parameterResult;
    }

    @Nullable
    public static ScriptPreparedAction parse(@NotNull String str) {
        String[] split = str.split(" ");
        if (split.length == 0) return null;

        ScriptAction action = ScriptActions.getByName(split[0].replace("[", "").replace("]", ""));
        if (action == null) return null;

        String params = Stream.of(split).skip(1).collect(Collectors.joining(" "));
        ScriptPreparedAction entry = new ScriptPreparedAction(action, new ParameterResult());

        action.getParameters().forEach(parameter -> {
            Pattern pattern = Pattern.compile(parameter.getName() + PATTERN_PARAMETER);
            Matcher matcher = RegexUtil.getMatcher(pattern, params);
            if (RegexUtil.matcherFind(matcher)) {
                String raw = matcher.group(1);
                entry.getParameters().add(parameter, parameter.getParser().apply(raw));
            }
        });

        return entry;
    }

    @NotNull
    public String toRaw() {
        String action = "[" + this.getAction().getName() + "] ";
        String params = this.getParameters().getParams().entrySet().stream().map(entry -> {
            return entry.getKey().getName() + ":\"" + entry.getValue() + "\"";
        }).collect(Collectors.joining(" "));

        return action + params;
    }

    public ScriptAction getAction() {
        return action;
    }

    public ParameterResult getParameters() {
        return parameterResult;
    }

}
