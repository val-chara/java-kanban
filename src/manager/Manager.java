package manager;

public class Manager {

    public static TaskManager getDefault() {

        return new FileBackedTaskManager();
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}
