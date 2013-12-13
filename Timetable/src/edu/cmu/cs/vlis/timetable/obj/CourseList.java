package edu.cmu.cs.vlis.timetable.obj;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class CourseList {

    private static class CourseListHolder {
        public static final CourseList INSTANCE = new CourseList();
    }

    public static CourseList getInstance() {
        return CourseListHolder.INSTANCE;
    }

    private HashMap<Integer, Course> data;

    public void loadData(JsonNode node) throws JsonParseException, JsonMappingException,
            IOException {
        this.data = new HashMap<Integer, Course>();
        Iterator<JsonNode> elements = node.elements();
        while (elements.hasNext()) {
            JsonNode inner = elements.next();
            Course course = new Course();
            course.setCourse_code(inner.get("course_code").asText());
            course.setId(inner.get("id").asInt());
            course.setSection(inner.get("section").asText());
            data.put(course.getId(), course);
        }
    }

    public HashMap<Integer, Course> getData() {
        return data;
    }

    public void invalidate() {
        data.clear();
    }

    public Course[] getCourseArray() {
        if (data == null) return null;
        return data.values().toArray(new Course[data.size()]);
    }
}
