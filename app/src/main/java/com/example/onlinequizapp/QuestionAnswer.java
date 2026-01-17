package com.example.onlinequizapp;

public class QuestionAnswer {

    static String[] javaQuestions = {
            "Java is a ____ language.",
            "Which keyword is used to create an object?",
            "Which company developed Java?",
            "Which method is the entry point of Java program?"
    };

    static String[][] javaChoices = {
            {"High-level", "Low-level", "Machine", "Assembly"},
            {"class", "new", "void", "this"},
            {"Google", "Microsoft", "Sun Microsystems", "Apple"},
            {"start()", "main()", "run()", "init()"}
    };

    static String[] javaCorrect = {
            "High-level",
            "new",
            "Sun Microsystems",
            "main()"
    };

    static String[] isdQuestions = {
            "ISD stands for?",
            "Which SDLC model is linear?",
            "Feasibility study is done in which phase?",
            "Which diagram shows system flow?"
    };

    static String[][] isdChoices = {
            {"Information System Design", "Information System Development", "Internal System Design", "Integrated System Data"},
            {"Agile", "Waterfall", "Spiral", "RAD"},
            {"Design", "Testing", "Planning", "Implementation"},
            {"Flowchart", "ER Diagram", "Use Case", "Class Diagram"}
    };

    static String[] isdCorrect = {
            "Information System Development",
            "Waterfall",
            "Planning",
            "Flowchart"
    };

    static String[] microQuestions = {
            "Which microprocessor has 16-bit architecture?",
            "How many pins does Intel 8086 have?",
            "Which register stores instruction?",
            "What is the full form of ALU?"
    };

    static String[][] microChoices = {
            {"8085", "8086", "8051", "8031"},
            {"16", "28", "40", "34"},
            {"AX", "BX", "IP", "SP"},
            {"Arithmetic Logic Unit", "Advanced Logic Unit", "Array Logic Unit", "Automatic Logic Unit"}
    };

    static String[] microCorrect = {
            "8086",
            "40",
            "IP",
            "Arithmetic Logic Unit"
    };

    public static String[] getQuestions(String subject) {
        switch (subject) {
            case "java":
                return javaQuestions;
            case "isd":
                return isdQuestions;
            case "micro":
                return microQuestions;
            default:
                return new String[]{};
        }
    }

    public static String[][] getChoices(String subject) {
        switch (subject) {
            case "java":
                return javaChoices;
            case "isd":
                return isdChoices;
            case "micro":
                return microChoices;
            default:
                return new String[][]{};
        }
    }

    public static String[] getCorrectAnswers(String subject) {
        switch (subject) {
            case "java":
                return javaCorrect;
            case "isd":
                return isdCorrect;
            case "micro":
                return microCorrect;
            default:
                return new String[]{};
        }
    }
}