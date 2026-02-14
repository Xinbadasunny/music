package com.vocalcoach.infrastructure.training.gateway;

import com.vocalcoach.domain.training.Course;
import com.vocalcoach.domain.training.TrainingProgress;
import com.vocalcoach.domain.training.gateway.TrainingGateway;
import com.vocalcoach.infrastructure.training.dataobject.TrainingProgressDO;
import com.vocalcoach.infrastructure.training.repository.TrainingProgressRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TrainingGatewayImpl implements TrainingGateway {

    @Resource
    private TrainingProgressRepository progressRepository;

    private static final List<Course> DEFAULT_COURSES = new ArrayList<>();

    @PostConstruct
    public void initCourses() {
        if (DEFAULT_COURSES.isEmpty()) {
            DEFAULT_COURSES.addAll(createDefaultCourses());
        }
    }

    private List<Course> createDefaultCourses() {
        List<Course> courses = new ArrayList<>();

        Course scaleCourse = new Course();
        scaleCourse.setId("scale");
        scaleCourse.setName("音阶训练");
        scaleCourse.setIcon("🎹");
        scaleCourse.setDescription("基础音阶练习，提升音准和音域");
        List<Course.Exercise> scaleExercises = new ArrayList<>();
        scaleExercises.add(createExercise("major_scale", "大调音阶", "练习C大调音阶上行和下行", 90,
                Arrays.asList(60, 62, 64, 65, 67, 69, 71, 72), 80, "从中央C开始，依次演唱Do-Re-Mi-Fa-Sol-La-Si-Do"));
        scaleExercises.add(createExercise("minor_scale", "小调音阶", "练习A小调音阶", 90,
                Arrays.asList(57, 59, 60, 62, 64, 65, 67, 69), 80, "从A开始演唱小调音阶"));
        scaleExercises.add(createExercise("chromatic", "半音阶", "练习半音阶，提升音准精度", 120,
                Arrays.asList(60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72), 60, "逐个半音上行演唱"));
        scaleCourse.setExercises(scaleExercises);
        courses.add(scaleCourse);

        Course breathCourse = new Course();
        breathCourse.setId("breath");
        breathCourse.setName("气息训练");
        breathCourse.setIcon("🌬️");
        breathCourse.setDescription("提升气息控制和呼吸稳定性");
        List<Course.Exercise> breathExercises = new ArrayList<>();
        breathExercises.add(createExercise("long_tone", "长音练习", "持续稳定地演唱长音", 120,
                Arrays.asList(67), 60, "深呼吸后用腹式呼吸支撑，尽可能长时间保持稳定"));
        breathExercises.add(createExercise("breath_control", "渐强渐弱", "练习声音的强弱控制", 90,
                Arrays.asList(67), 60, "用同一个音高，从弱到强再回到弱"));
        breathExercises.add(createExercise("staccato", "断音练习", "短促有力的断音", 60,
                Arrays.asList(67, 67, 67, 67), 100, "用短促有力的方式演唱同一个音"));
        breathCourse.setExercises(breathExercises);
        courses.add(breathCourse);

        Course rhythmCourse = new Course();
        rhythmCourse.setId("rhythm");
        rhythmCourse.setName("节奏训练");
        rhythmCourse.setIcon("🥁");
        rhythmCourse.setDescription("提升节奏感和节拍掌控能力");
        List<Course.Exercise> rhythmExercises = new ArrayList<>();
        rhythmExercises.add(createExercise("quarter_notes", "四分音符练习", "基础的节拍练习", 60,
                Arrays.asList(60, 60, 60, 60), 80, "跟着节拍器演唱四分音符"));
        rhythmExercises.add(createExercise("eighth_notes", "八分音符练习", "更快的节奏练习", 80,
                Arrays.asList(60, 60, 60, 60, 60, 60, 60, 60), 75, "每拍两个音，保持稳定"));
        rhythmExercises.add(createExercise("syncopation", "切分音练习", "练习切分节奏", 70,
                Arrays.asList(60, 62, 64, 62), 70, "注意重音位置的变化"));
        rhythmCourse.setExercises(rhythmExercises);
        courses.add(rhythmCourse);

        Course pitchCourse = new Course();
        pitchCourse.setId("pitch");
        pitchCourse.setName("音准训练");
        pitchCourse.setIcon("🎯");
        pitchCourse.setDescription("提升音准精确度");
        List<Course.Exercise> pitchExercises = new ArrayList<>();
        pitchExercises.add(createExercise("interval_2nd", "二度音程", "练习相邻音的音准", 80,
                Arrays.asList(60, 62, 60, 62), 85, "注意两个音之间的距离"));
        pitchExercises.add(createExercise("interval_3rd", "三度音程", "练习三度音程", 80,
                Arrays.asList(60, 64, 60, 64), 80, "大三度和小三度的区别"));
        pitchExercises.add(createExercise("interval_5th", "五度音程", "练习五度音程", 80,
                Arrays.asList(60, 67, 60, 67), 75, "五度是和谐的音程"));
        pitchCourse.setExercises(pitchExercises);
        courses.add(pitchCourse);

        Course vibratoCourse = new Course();
        vibratoCourse.setId("vibrato");
        vibratoCourse.setName("颤音训练");
        vibratoCourse.setIcon("〰️");
        vibratoCourse.setDescription("学习和掌握颤音技巧");
        List<Course.Exercise> vibratoExercises = new ArrayList<>();
        vibratoExercises.add(createExercise("slow_vibrato", "慢速颤音", "缓慢的颤音练习", 60,
                Arrays.asList(67), 70, "放松喉咙，让声音自然波动"));
        vibratoExercises.add(createExercise("medium_vibrato", "中速颤音", "中等速度的颤音", 80,
                Arrays.asList(67), 65, "保持稳定的颤音频率"));
        vibratoExercises.add(createExercise("fast_vibrato", "快速颤音", "快速的颤音练习", 100,
                Arrays.asList(67), 60, "控制颤音的幅度和速度"));
        vibratoCourse.setExercises(vibratoExercises);
        courses.add(vibratoCourse);

        Course rangeCourse = new Course();
        rangeCourse.setId("range");
        rangeCourse.setName("音域拓展");
        rangeCourse.setIcon("📈");
        rangeCourse.setDescription("安全地拓展音域范围");
        List<Course.Exercise> rangeExercises = new ArrayList<>();
        rangeExercises.add(createExercise("low_range", "低音区练习", "拓展低音区", 70,
                Arrays.asList(48, 50, 52, 53, 55), 70, "放松喉咙，让声音下沉"));
        rangeExercises.add(createExercise("mid_range", "中音区练习", "巩固中音区", 80,
                Arrays.asList(60, 62, 64, 65, 67), 80, "保持声音的稳定和饱满"));
        rangeExercises.add(createExercise("high_range", "高音区练习", "拓展高音区", 70,
                Arrays.asList(72, 74, 76, 77, 79), 65, "使用头声，不要挤压喉咙"));
        rangeCourse.setExercises(rangeExercises);
        courses.add(rangeCourse);

        return courses;
    }

    private Course.Exercise createExercise(String id, String name, String description, int bpm,
                                            List<Integer> notes, int passingScore, String tips) {
        Course.Exercise exercise = new Course.Exercise();
        exercise.setId(id);
        exercise.setName(name);
        exercise.setDescription(description);
        exercise.setBpm(bpm);
        exercise.setNotes(notes);
        exercise.setPassingScore(passingScore);
        exercise.setTips(tips);
        return exercise;
    }

    @Override
    public List<Course> findAllCourses() {
        return new ArrayList<>(DEFAULT_COURSES);
    }

    @Override
    public Optional<Course> findCourseById(String courseId) {
        return DEFAULT_COURSES.stream()
                .filter(c -> c.getId().equals(courseId))
                .findFirst();
    }

    @Override
    public List<TrainingProgress> findAllProgress() {
        return progressRepository.findAll().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TrainingProgress> findProgress(String courseId, String exerciseId) {
        return progressRepository.findByCourseIdAndExerciseId(courseId, exerciseId)
                .map(this::toEntity);
    }

    @Override
    public TrainingProgress saveProgress(TrainingProgress progress) {
        TrainingProgressDO progressDO = toDO(progress);
        TrainingProgressDO saved = progressRepository.save(progressDO);
        return toEntity(saved);
    }

    @Override
    public int countCompletedExercises() {
        return progressRepository.countByCompletedTrue();
    }

    @Override
    public int countTotalExercises() {
        return DEFAULT_COURSES.stream()
                .mapToInt(c -> c.getExercises() != null ? c.getExercises().size() : 0)
                .sum();
    }

    private TrainingProgress toEntity(TrainingProgressDO progressDO) {
        TrainingProgress progress = new TrainingProgress();
        progress.setId(progressDO.getId());
        progress.setCourseId(progressDO.getCourseId());
        progress.setExerciseId(progressDO.getExerciseId());
        progress.setBestScore(progressDO.getBestScore());
        progress.setAttempts(progressDO.getAttempts());
        progress.setCompleted(progressDO.getCompleted());
        progress.setLastPracticeTime(progressDO.getLastPracticeTime());
        return progress;
    }

    private TrainingProgressDO toDO(TrainingProgress progress) {
        TrainingProgressDO progressDO = new TrainingProgressDO();
        progressDO.setId(progress.getId());
        progressDO.setCourseId(progress.getCourseId());
        progressDO.setExerciseId(progress.getExerciseId());
        progressDO.setBestScore(progress.getBestScore());
        progressDO.setAttempts(progress.getAttempts());
        progressDO.setCompleted(progress.getCompleted());
        progressDO.setLastPracticeTime(progress.getLastPracticeTime());
        return progressDO;
    }
}
