export default class TeacherDashboardQuestionStats {
  id!: number;
  numAvailable!: number;
  answeredQuestionsUnique!: number;
  averageQuestionsAnswered!: number;
  courseExecutionYear!: number;

  constructor(jsonObj?: TeacherDashboardQuestionStats) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.numAvailable = jsonObj.numAvailable;
      this.answeredQuestionsUnique = jsonObj.answeredQuestionsUnique;
      this.averageQuestionsAnswered = jsonObj.averageQuestionsAnswered;
      this.courseExecutionYear = jsonObj.courseExecutionYear;
    }
  }
}
