package com.example.quizapp.quiz;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String QUESTION_TABLE = "Question";
    public static final String QUESTION_ID = "question_id";
    public static final String QUESTION_TEXT = "question_text";
    public static final String PRACTICED_CORRECT = "practiced_correct";

    public static final String ANSWER_TABLE = "Answer";
    public static final String ANSWER_ID = "answer_id";
    public static final String ANSWER_TEXT = "answer_text";
    public static final String CORRECT = "correct";


    public DatabaseHelper(@Nullable Context context) {
        super(context, "questions.db", null, 6);
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

   @Override
    public void onCreate(SQLiteDatabase db) {
        // table definitions
        String statement = "CREATE TABLE IF NOT EXISTS " + QUESTION_TABLE + " ( " +
                QUESTION_ID + "	INTEGER PRIMARY KEY, " +
                QUESTION_TEXT + " TEXT NOT NULL, " +
                PRACTICED_CORRECT + " INTEGER DEFAULT 0);";
        db.execSQL(statement);

        statement = "CREATE TABLE IF NOT EXISTS " + ANSWER_TABLE + " ( " +
                ANSWER_ID + " INTEGER PRIMARY KEY, " +
                QUESTION_ID + " INTEGER NOT NULL, " +
                ANSWER_TEXT + " INTEGER Not Null, " +
                CORRECT + " INTEGER Not Null, " +
                "FOREIGN KEY (" + QUESTION_ID + ")" +
                "REFERENCES " + QUESTION_TABLE + " (" + QUESTION_ID + ") ON DELETE CASCADE);";
        db.execSQL(statement);

        // create initial values
        getInitialQuestions().forEach(q -> {
            if (!insertQuestionIfComplete(db, q)) {
                Log.d("DatabaseHelper", "Error when creating Question" + q.getQuestionText());
            }
        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DELETE FROM " + QUESTION_TABLE + ";");
        db.execSQL("DELETE FROM " + ANSWER_TABLE + ";");

        onCreate(db);
    }

    /**
     * Get a random Question which is not yet answered correctly from the database.
     *
     * @return a random Question object
     */
    public Optional<Question> getPracticeQuestion() {
        SQLiteDatabase db = this.getReadableDatabase();

        String statement = "SELECT * FROM " + QUESTION_TABLE + " " +
                "WHERE " + PRACTICED_CORRECT + " == 0 " +
                "ORDER BY RANDOM() LIMIT 1;";
        Cursor cursor = db.rawQuery(statement, null);

        Optional<Question> value = Optional.empty();

        if (cursor.moveToFirst()) {
            // get the found result and fill the list of answers
            int questionID = cursor.getInt(0);
            String questionText = cursor.getString(1);
            boolean answeredCorrectly = cursor.getInt(2) == 1;

            List<Answer> answers = this.getAnswerToQuestion(questionID);

            if (answers.size() > 0) {
                value = Optional.of(new Question(questionID, questionText, answers, answeredCorrectly));
            }
        }

        cursor.close();
        db.close();

        return value;
    }

    /**
     * Get a set of distinct questions from the database.
     *
     * @param number number of distinct questions to get
     * @return a list of Question objects
     */
    public List<Question> getTestQuestionList(int number) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Question> questions = new ArrayList<>();

        String statement = "SELECT * FROM " + QUESTION_TABLE + " " +
                "ORDER BY RANDOM() LIMIT " + String.valueOf(number) + ";";
        Cursor cursor = db.rawQuery(statement, null);

        if (cursor.moveToFirst()) {
            do {
                // get the found result and fill the list of answers
                int questionID = cursor.getInt(0);
                String questionText = cursor.getString(1);
                boolean answeredCorrectly = cursor.getInt(2) == 1;

                List<Answer> answers = this.getAnswerToQuestion(questionID);

                if (answers.size() > 0) {
                    questions.add(new Question(questionID, questionText, answers, answeredCorrectly));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return questions;
    }

    /**
     * Reset the answered correctly flag for each question to false.
     */
    public void resetQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();

        String statement = "UPDATE " + QUESTION_TABLE +
                " SET " + PRACTICED_CORRECT + " = 0; ";
        db.execSQL(statement);

        db.close();
    }

    /**
     * Set the answered correctly state of a question in the database identified by it's id.
     *
     * @param questionID        the questionId of the question
     * @param answeredCorrectly information whether the question was answered correctly or not
     */
    public void setQuestionsAnsweredCorrectly(int questionID, boolean answeredCorrectly) {

        SQLiteDatabase db = this.getWritableDatabase();
        int value = answeredCorrectly ? 1 : 0;

        String statement = "UPDATE " + QUESTION_TABLE +
                " SET " + PRACTICED_CORRECT + " = " + String.valueOf(value) +
                " WHERE " + QUESTION_ID + " == " + String.valueOf(questionID) + ";";
        db.execSQL(statement);

        db.close();
    }

    /**
     * Insert a Question object into the databse. For this to work, the Question has to have a
     * question text and it's list of answers has to have at least one entry. Also none of the
     * answers can have an empty answer text
     *
     * @param db       the sqlite database to insert into
     * @param question the question which should be inserted
     * @return information whether the insertion was successful
     */
    private boolean insertQuestionIfComplete(SQLiteDatabase db, Question question) {
        // check if question and it's answers are in the correct format before inserting
        if (!question.getQuestionText().equals("") && question.getAnswers().size() > 0) {
            for (Answer answer : question.getAnswers()) {
                if (answer.getAnswerText().equals("")) {
                    return false;
                }
            }

            // insert question and get it's questionId as it is in the database
            String insertQuestion = "INSERT INTO " + QUESTION_TABLE + " (" + QUESTION_TEXT + ") VALUES ('" + question.getQuestionText() + "')";
            db.execSQL(insertQuestion);

            Cursor cursor = db.rawQuery("SELECT * FROM " + QUESTION_TABLE + " WHERE rowid == LAST_INSERT_ROWID()", null);
            cursor.moveToFirst();
            int questionID = cursor.getInt(0);

            // insert the answers for the question
            question.getAnswers().forEach(a -> {
                String insertAnswer = "INSERT INTO " + ANSWER_TABLE + " (" + ANSWER_TEXT + ", "
                        + QUESTION_ID + ", " + CORRECT + ") VALUES ('" + a.getAnswerText() + "', "
                        + questionID + ", " + String.valueOf(a.isCorrect() ? 1 : 0) + ");";
                db.execSQL(insertAnswer);
            });
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the list of Answers to a question specified by it's questionId.
     *
     * @param questionId the questionId of the question
     * @return the list of answers for the question
     */
    private List<Answer> getAnswerToQuestion(int questionId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String statement = "SELECT * FROM Answer " +
                "WHERE " + QUESTION_ID + " == ?;";
        Cursor cursor = db.rawQuery(statement, new String[]{String.valueOf(questionId)});

        List<Answer> answers = new ArrayList<>();

        if (cursor.moveToFirst()) {
            // for each answer found, create an answer object and add it to the list
            do {
                int answerID = cursor.getInt(0);
                String answerText = cursor.getString(2);
                boolean correct = cursor.getInt(3) == 1;

                answers.add(new Answer(answerID, answerText, correct));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return answers;
    }

    /**
     * Create a List of Questions to be inserted into the database.
     *
     * @return the list of Question objects
     */
    private List<Question> getInitialQuestions() {
        List<Question> questions = new ArrayList<>();

        Question question = new Question("Was beschreibt der Begriff der Tragfähigkeit?", new ArrayList<>());
        question.getAnswers().add(new Answer("Die Zahl der Menschen, die in einer bestimmten Region auf Dauer unter bestimmten Bedingungen leben können.", true));
        question.getAnswers().add(new Answer("Die Zahl der Menschen, die ein durchschnittlicher Mensch tragen kann.", false));
        questions.add(question);

        question = new Question("Was beschreibt die Mortalität?", new ArrayList<>());
        question.getAnswers().add(new Answer("Die Mortalität ist die Sterbeziffer und wird durch die Zahl der Gestorbenen in einem Jahr bezogen auf 1000 Personen der Bevölkerung erfasst.", true));
        question.getAnswers().add(new Answer("Im Zuge der Mortalität wird altersbedingt auch oft die Säuglingssterblichkeit ausgewiesen.", true));
        questions.add(question);

        question = new Question("Welche dieser Formen sind typische Formen von Alterspyramiden", new ArrayList<>());
        question.getAnswers().add(new Answer("Dreieck", true));
        question.getAnswers().add(new Answer("Pyramide", true));
        question.getAnswers().add(new Answer("Kugel", false));
        question.getAnswers().add(new Answer("Urne", true));
        question.getAnswers().add(new Answer("Stachel", false));
        questions.add(question);

        question = new Question("Was beschreibt der CO2 Fußabdruck?", new ArrayList<>());
        question.getAnswers().add(new Answer("Die Größe der Waldfläche, die benötigt wird, um alle CO2 Emissionen aufzunehmen.", true));
        question.getAnswers().add(new Answer("Das Gesamtgewicht aller CO2 Emissionen gemessen in Tonnen.", false));
        questions.add(question);

        question = new Question("Welches sind Indikatoren zur Einordnung in die Heirarchie der Global Cities?", new ArrayList<>());
        question.getAnswers().add(new Answer("Sitz von Börsen", false));
        question.getAnswers().add(new Answer("Sitz von NROs", false));
        question.getAnswers().add(new Answer("Führende Seehäfen nach dem Umschlag", true));
        questions.add(question);

        question = new Question("Wovon spricht man, wenn man über Standortfaktoren spricht?", new ArrayList<>());
        question.getAnswers().add(new Answer("Harte und Weiche Standortfaktoren", true));
        question.getAnswers().add(new Answer("Sichtbare und Unsichtbare Standortfaktoren", false));
        question.getAnswers().add(new Answer("Direkte und Indirekte Standortfaktoren", false));
        questions.add(question);

        question = new Question("Was bedeutet arid?", new ArrayList<>());
        question.getAnswers().add(new Answer("Es verdunstet im Durchschnitt mehr Wasser pro Jahr als Niederschlag fällt.", true));
        question.getAnswers().add(new Answer("Es fällt im Durschnitt pro Jahr mehr Wasser als verdunstet.", false));
        questions.add(question);

        question = new Question("Was sind Merkmale von sanftem Tourismus?", new ArrayList<>());
        question.getAnswers().add(new Answer("Schutz und Erhaltung der Landschaft", true));
        question.getAnswers().add(new Answer("Schaffung von innerreginal verankerten Tourismuswirtschaft", true));
        question.getAnswers().add(new Answer("Aufenthalte in abgelegenen Hotels", false));
        questions.add(question);

        question = new Question("Welche dieser Touristischen Potenziale sind den kultur- und sozialgeograpthischen Einflüssen zuzuordnen", new ArrayList<>());
        question.getAnswers().add(new Answer("(exotische) Tier und Pflanzenwelt", false));
        question.getAnswers().add(new Answer("Klima", false));
        question.getAnswers().add(new Answer("Sicherheit während des Aufenthalts", true));
        question.getAnswers().add(new Answer("kulturelles Angebot", true));
        question.getAnswers().add(new Answer("infrastrukturielles Angebot", true));
        questions.add(question);

        question = new Question("Welche dieser Begriffe stehen für Syndrome in der Syndromgruppe \"Nutzung\"", new ArrayList<>());
        question.getAnswers().add(new Answer("SAHEL", true));
        question.getAnswers().add(new Answer("DUST BOWL", true));
        question.getAnswers().add(new Answer("VERBRANNTE ERDE", true));
        question.getAnswers().add(new Answer("LANDFLUCHT", true));
        questions.add(question);

        question = new Question("Welche Wirtschaftsgebiete sind generell dem primären Wirtschaftssektor zuzuordnen?", new ArrayList<>());
        question.getAnswers().add(new Answer("Dienstleistungen", false));
        question.getAnswers().add(new Answer("Verarbeitung von Rohstoffen", false));
        question.getAnswers().add(new Answer("Landwirtschaft", true));
        questions.add(question);

        return questions;
    }

}

