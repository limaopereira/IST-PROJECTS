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
import TeacherDashboardQuizStats from '@/models/dashboard/TeacherDashboardQuizStats';

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
export default class QuizStatsBarChart extends Vue {
  @Prop() readonly quizStats!: TeacherDashboardQuizStats[];
  data: any;
  options: any;

  created() {
    this.updateQuizStats();
  }

  @Watch('quizStats', { immediate: true, deep: true })
  updateQuizStats() {
    const reversedStats = [...this.quizStats].reverse();
    this.data = {
      labels: reversedStats.map((stat, index) =>
        index === reversedStats.length - 1
          ? `${stat.courseExecutionYear} (current)`
          : stat.courseExecutionYear
      ),
      datasets: [
        {
          label: 'Quizzes: Total Available',
          backgroundColor: '#C13627ff',

          data: reversedStats.map((stat) => stat.numQuizzes),
        },
        {
          label: 'Quizzes: Solved (Unique)',
          backgroundColor: '#2481BAff',

          data: reversedStats.map((stat) => stat.numUniqueAnsweredQuizzes),
        },
        {
          label: 'Quizzes: Solved (Unique, Average Per Student)',
          backgroundColor: '#13BD9Dff',

          data: reversedStats.map((stat) => stat.averageQuizzesSolved),
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
