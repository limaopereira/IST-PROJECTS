import TeacherDashboardStudentStats from '@/models/dashboard/TeacherDashboardStudentStats';
import TeacherDashboardQuestionStats from '@/models/dashboard/TeacherDashboarQuestionStats';
import TeacherDashboardQuizStats from '@/models/dashboard/TeacherDashboardQuizStats';

export default class TeacherDashboard {
  id!: number;
  studentStats!: TeacherDashboardStudentStats[];
  questionStats!: TeacherDashboardQuestionStats[];
  quizStats!: TeacherDashboardQuizStats[];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.studentStats = jsonObj.studentStats.map(
        (s) => new TeacherDashboardStudentStats(s)
      );
      this.questionStats = jsonObj.questionStats.map(
        (s) => new TeacherDashboardQuestionStats(s)
      );
      this.quizStats = jsonObj.quizStats.map(
        (s) => new TeacherDashboardQuizStats(s)
      );
    }
  }
}
