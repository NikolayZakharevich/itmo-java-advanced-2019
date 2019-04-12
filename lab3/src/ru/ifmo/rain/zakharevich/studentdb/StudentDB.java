package ru.ifmo.rain.zakharevich.studentdb;

import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentGroupQuery;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements StudentQuery {

    private Comparator<Student> NAME_COMPARATOR = Comparator.comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .thenComparing(Student::getId);

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return mappedList(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return mappedList(students, Student::getLastName);
    }

    @Override
    public List<String> getGroups(List<Student> students) {
        return mappedList(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return mappedList(students, student -> student.getFirstName() + " " + student.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students.stream()
                .map(Student::getFirstName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMinStudentFirstName(List<Student> students) {
        return students.stream()
                .min(Comparator.comparingInt(Student::getId))
                .map(Student::getFirstName)
                .orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortedList(students, Comparator.comparingInt(Student::getId));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortedList(students, NAME_COMPARATOR);
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return filteredSortedList(students, student -> student.getFirstName().equals(name), NAME_COMPARATOR);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return filteredSortedList(students, student -> student.getLastName().equals(name), NAME_COMPARATOR);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return filteredSortedList(students, student -> student.getGroup().equals(group), NAME_COMPARATOR);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        return students.stream()
                .filter(student -> student.getGroup().equals(group))
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName, BinaryOperator.minBy(String::compareTo)));
    }

    private Stream<String> map(List<Student> students, Function<Student, String> mapper) {
        return students.stream().map(mapper);
    }

    private List<String> mappedList(List<Student> students, Function<Student, String> mapper) {
        return map(students, mapper).collect(Collectors.toList());
    }

    private Stream<Student> filter(Collection<Student> students, Predicate<Student> predicate) {
        return students.stream().filter(predicate);
    }

    private List<Student> sortedList(Stream<Student> stream, Comparator<Student> comparator) {
        return stream.sorted(comparator).collect(Collectors.toList());
    }

    private List<Student> sortedList(Collection<Student> students, Comparator<Student> comparator) {
        return sortedList(students.stream(), comparator);
    }

    private List<Student> filteredSortedList(Collection<Student> students, Predicate<Student> predicate,
                                             Comparator<Student> comparator) {
        return sortedList(filter(students, predicate), comparator);
    }
}
