package tests.model;

import model.Epic;
import model.Status;

import static org.junit.jupiter.api.Assertions.*;

import model.Subtask;
import org.junit.jupiter.api.Test;

class SubtaskTest {

    @Test
    void subtaskShouldNotBeItsOwnEpic() {
        Epic epic = new Epic("Эпик 2", "Описание второго эпика", Status.NEW);
        Subtask subtask = new Subtask("Подзадача 2", "Описание подзадачи №2", Status.NEW, epic.getId());
        subtask.setId(2);

        assertNotEquals(subtask.getId(), subtask.getEpicId(), "Подзадача не должна быть своим же эпиком");
    }

}