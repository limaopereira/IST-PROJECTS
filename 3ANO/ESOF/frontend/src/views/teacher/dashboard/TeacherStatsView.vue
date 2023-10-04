<template>
  <div class="container">
    <h2>Statistics for this course execution</h2>
    <div v-if="teacherDashboard != null" class="stats-container">
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.studentStats[0].numStudents"
            data-cy="totalStudents"
          />
        </div>
        <div class="project-name">
          <p>Number of Students</p>
        </div>
      </div>
      <div class="items">
        <div ref="numMore75CorrectQuestions" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.studentStats[0].numMore75CorrectQuestions"
            data-cy="numMore75CorrectQuestions"
          />
        </div>
        <div class="project-name">
          <p>Number of Students who Solved >= 75% Questions</p>
        </div>
      </div>
      <div class="items">
        <div ref="numAtLeast3Quizzes" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.studentStats[0].numAtLeast3Quizzes"
            data-cy="numAtLeast3Quizzes"
          />
        </div>
        <div class="project-name">
          <p>Number of Students who Solved >= 3 Quizzes</p>
        </div>
      </div>
      <div class="items">
        <div ref="numAvailable" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.questionStats[0].numAvailable"
            data-cy="numAvailable"
          />
        </div>
        <div class="project-name">
          <p>Number of Questions</p>
        </div>
      </div>
      <div class="items">
        <div ref="answeredQuestionsUnique" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.questionStats[0].answeredQuestionsUnique"
            data-cy="answeredQuestionsUnique"
          />
        </div>
        <div class="project-name">
          <p>Number of Questions Solved (Unique)</p>
        </div>
      </div>
      <div class="items">
        <div ref="averageQuestionsAnswered" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.questionStats[0].averageQuestionsAnswered"
            data-cy="averageQuestionsAnswered"
          />
        </div>
        <div class="project-name">
          <p>
            Number of Questions Correctly Solved (Unique, Average Per Student)
          </p>
        </div>
      </div>
      <div class="items">
        <div ref="numQuizzes" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.quizStats[0].numQuizzes"
            data-cy="numQuizzes"
          />
        </div>
        <div class="project-name">
          <p>Number of Quizzes</p>
        </div>
      </div>
      <div class="items">
        <div ref="numUniqueAnsweredQuizzes" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.quizStats[0].numUniqueAnsweredQuizzes"
            data-cy="numUniqueAnsweredQuizzes"
          />
        </div>
        <div class="project-name">
          <p>Number of Quizzes Solved (Unique)</p>
        </div>
      </div>
      <div class="items">
        <div ref="averageQuizzesSolved" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.quizStats[0].averageQuizzesSolved"
            data-cy="averageQuizzesSolved"
          />
        </div>
        <div class="project-name">
          <p>Number of Quizzes Solved (Unique, Average Per Student)</p>
        </div>
      </div>
      <div class="items">
        <student-stats-bar-chart
          v-if="teacherDashboard.studentStats.length > 1"
          :studentStats="teacherDashboard.studentStats"
        />
      </div>
      <div class="items">
        <question-stats-bar-chart
          v-if="teacherDashboard.questionStats.length > 1"
          :questionStats="teacherDashboard.questionStats"
        />
      </div>
      <div class="items">
        <quiz-stats-bar-chart
          v-if="teacherDashboard.quizStats.length > 1"
          :quizStats="teacherDashboard.quizStats"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import TeacherDashboard from '@/models/dashboard/TeacherDashboard';
import StudentStatsBarChart from '@/views/teacher/dashboard/StudentStatsBarChart.vue';
import QuestionStatsBarChart from '@/views/teacher/dashboard/QuestionStatsBarChart.vue';
import QuizStatsBarChart from '@/views/teacher/dashboard/QuizStatsBarChart.vue';

@Component({
  components: {
    AnimatedNumber,
    StudentStatsBarChart,
    QuestionStatsBarChart,
    QuizStatsBarChart,
  },
})
export default class TeacherStatsView extends Vue {
  @Prop() readonly dashboardId!: number;
  teacherDashboard: TeacherDashboard | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.teacherDashboard = await RemoteServices.getTeacherDashboard();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }

  .bar-chart {
    background-color: rgba(255, 255, 255, 0.9);
    height: 400px;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
