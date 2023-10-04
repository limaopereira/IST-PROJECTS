<template>
  <Bar :chartData="data" :chartOptions="options" />
</template>

<script lang="ts">
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from 'chart.js';

import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import { Bar } from 'vue-chartjs/legacy';
import TeacherDashboardStudentStats from '@/models/dashboard/TeacherDashboardStudentStats';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

@Component({
  components: {
    Bar,
  },
})
export default class StudentStatsBarChart extends Vue {
  @Prop() readonly studentStats!: TeacherDashboardStudentStats[];
  data: any;
  options: any;

  created() {
    this.updateStudentStats();
  }

  @Watch('studentStats', { immediate: true, deep: true })
  updateStudentStats() {
    const reversedStats = [...this.studentStats].reverse();
    this.data = {
      labels: reversedStats.map((stat, index) =>
        index === reversedStats.length - 1
          ? `${stat.courseExecutionYear} (current)`
          : stat.courseExecutionYear
      ),
      datasets: [
        {
          label: 'Total Number of Students',
          backgroundColor: '#C13627ff',

          data: reversedStats.map((stat) => stat.numStudents),
        },
        {
          label: 'Students who Solved >= 75% of Questions',
          backgroundColor: '#2481BAff',

          data: reversedStats.map((stat) => stat.numMore75CorrectQuestions),
        },
        {
          label: 'Students who Solved >= 3 Quizzes',
          backgroundColor: '#13BD9Dff',

          data: reversedStats.map((stat) => stat.numAtLeast3Quizzes),
        },
      ],
    };
    this.options = {
      responsive: true,
      maintainAspectRatio: false,
    };
  }
}
</script>
