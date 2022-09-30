package massim.javaagents.massimworld.actions;

import java.util.Set;

/**
 * The ActionResult enum encapsulates the value of the lastActionResult percept.
 */
public enum ActionResult {

    UNPROCESSED("unprocessed"),
    SUCCESS("success"),
    PARTIAL_SUCCESS("partial_success"),
    FAILED("failed"),
    FAILED_RANDOM("failed_random"),
    FAILED_PARAMETER("failed_parameter"),
    FAILED_PATH("failed_path"),
    FAILED_PARTNER("failed_partner"),
    FAILED_TARGET("failed_target"),
    FAILED_BLOCKED("failed_blocked"),
    FAILED_STATUS("failed_status"),
    FAILED_RESOURCES("failed_resources"),
    FAILED_LOCATION("failed_location"),
    FAILED_ROLE("failed_role");

    private final String name;

    ActionResult(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Set<ActionResult> errorResults() {
        return Set.of(FAILED, FAILED_PARAMETER, FAILED_PATH, FAILED_TARGET, FAILED_STATUS, FAILED_BLOCKED, FAILED_LOCATION, FAILED_ROLE);
    }


    public static ActionResult byName(String name) {
        for (ActionResult actionResult : ActionResult.values()) {
            if (actionResult.name.equalsIgnoreCase(name)) {
                return actionResult;
            }
        }
        throw new IllegalArgumentException("no action result for name " + name);
    }
}
