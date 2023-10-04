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
import TeacherDashboardQuestionStats from '@/models/dashboard/TeacherDashboarQuestionStats';

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
export default class QuestionStatsBarChart extends Vue {
  @Prop() readonly questionStats!: TeacherDashboardQuestionStats[];
  data: any;
  options: any;

  created() {
    this.updateQuestionStats();
  }

  @Watch('questionStats', { immediate: true, deep: true })
  updateQuestionStats() {
    const reversedStats = [...this.questionStats].reverse();
    this.data = {
      labels: reversedStats.map((stat, index) =>
        index === reversedStats.length - 1
          ? `${stat.courseExecutionYear} (current)`
          : stat.courseExecutionYear
      ),
      datasets: [
        {
          label: 'Questions: Total Available',
          backgroundColor: '#C13627ff',

          data: reversedStats.map((stat) => stat.numAvailable),
        },
        {
          label: 'Questions: Total Solved (Unique)',
          backgroundColor: '#2481BAff',

          data: reversedStats.map((stat) => stat.answeredQuestionsUnique),
        },
        {
          label: 'Questions: Correctly Solved (Unique, Average Per Student)',
          backgroundColor: '#13BD9Dff',

          data: reversedStats.map((stat) => stat.averageQuestionsAnswered),
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
