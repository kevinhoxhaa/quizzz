package commons.utils;

/**
 * An enumerator identifying the different types of questions:
 * CONSUMPTION - a multiple-choice question for an activity consumption
 * ESTIMATION - an open question for an activity consumption
 * CHOICE - a multiple-choice question about which activity is better than
 * the displayed one ("What will you do instead of?")
 * COMPARISON - a multiple-choice question for comparing the consumption
 * of two activities with the choices being 1 (greater), 2 (smaller) and 3 (equal)
 */
public enum QuestionType {
    CONSUMPTION,
    ESTIMATION,
    CHOICE,
    COMPARISON
}
