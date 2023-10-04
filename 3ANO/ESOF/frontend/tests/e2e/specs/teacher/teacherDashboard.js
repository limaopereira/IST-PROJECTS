describe('Teacher Dashboard', () => {
    let date;
    before(() => {
      cy.deleteQuestionsAndAnswers();
      cy.cleanDataCE();
      cy.request('http://localhost:8080/auth/demo/teacher')
        .as('loginResponse')
        .then((response) => {
          Cypress.env('token', response.body.token);
          return response;
        });
      date = new Date();
      cy.demoTeacherLogin();
      cy.initializeDataCE();
      cy.contains('Logout').click(); // existe forma de evitar isto?
    });
  
    after(() => {
        cy.deleteQuestionsAndAnswers();
        cy.cleanDataCE();
    });
  
    it('teacher accesses dashboard CE 2023 with correct values', () => {
        cy.intercept('GET', '**/teachers/dashboards/executions/*').as(
          'getTeacherDashboard'
        );
        
        cy.demoTeacherLogin();

        cy.wait(500);
        cy.contains('Change course').click();
        cy.wait(500);
        cy.contains('div', '1st Semester 2023/2024').next('div').find('button').click();
        
        cy.wait(500);
        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getTeacherDashboard');
  
        cy.get('canvas').eq(0).scrollIntoView().wait(5000).screenshot('2023_2024/result-screenshots/StudentStats');
        cy.get('canvas').eq(1).scrollIntoView().wait(5000).screenshot('2023_2024/result-screenshots/QuestionStats');
        cy.get('canvas').eq(2).scrollIntoView().wait(5000).screenshot('2023_2024/result-screenshots/QuizStats');
  

        cy.wait(500);
        cy.get('[data-cy="numAtLeast3Quizzes"]').should('contain',8); 
        cy.get('[data-cy="numMore75CorrectQuestions"]').should('contain',3); 
        cy.get('[data-cy="totalStudents"]').should('contain',20);
        
        cy.get('[data-cy="answeredQuestionsUnique"]').should('contain',40); 
        cy.get('[data-cy="averageQuestionsAnswered"]').should('contain',20); 
        cy.get('[data-cy="numAvailable"]').should('contain',60);
        
        cy.get('[data-cy="averageQuizzesSolved"]').should('contain',23); 
        cy.get('[data-cy="numQuizzes"]').should('contain',140); 
        cy.get('[data-cy="numUniqueAnsweredQuizzes"]').should('contain',28);
        
        cy.checkCharts('2023_2024','StudentStats');

        cy.contains('Logout').click();
  
        Cypress.on('uncaught:exception', (err, runnable) => {
          // returning false here prevents Cypress from
          // failing the test
          return false;
        });
      });  

      it('teacher accesses dashboard CE 2022 with correct values', () => {
        cy.intercept('GET', '**/teachers/dashboards/executions/*').as(
          'getTeacherDashboard'
        );
        
        cy.demoTeacherLogin();

        cy.wait(500);
        cy.contains('Change course').click();
        cy.wait(500);
        cy.contains('div', '1st Semester 2022/2023').next('div').find('button').click();
    
        cy.wait(500);
        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getTeacherDashboard');
    
        cy.get('canvas').eq(0).scrollIntoView().wait(5000).screenshot('2022_2023/result-screenshots/StudentStats');
        cy.get('canvas').eq(1).scrollIntoView().wait(5000).screenshot('2022_2023/result-screenshots/QuestionStats');
        cy.get('canvas').eq(2).scrollIntoView().wait(5000).screenshot('2022_2023/result-screenshots/QuizStats');
    
        cy.wait(500);
        cy.get('[data-cy="numAtLeast3Quizzes"]').should('contain',3); 
        cy.get('[data-cy="numMore75CorrectQuestions"]').should('contain',20); 
        cy.get('[data-cy="totalStudents"]').should('contain',8);
          
        cy.get('[data-cy="answeredQuestionsUnique"]').should('contain',60); 
        cy.get('[data-cy="averageQuestionsAnswered"]').should('contain',20); 
        cy.get('[data-cy="numAvailable"]').should('contain',80);
        
        cy.get('[data-cy="averageQuizzesSolved"]').should('contain',32); 
        cy.get('[data-cy="numQuizzes"]').should('contain',150); 
        cy.get('[data-cy="numUniqueAnsweredQuizzes"]').should('contain',82);
        
        cy.checkCharts('2022_2023','StudentStats');
    
        cy.contains('Logout').click();

        Cypress.on('uncaught:exception', (err, runnable) => {
          // returning false here prevents Cypress from
          // failing the test
          return false;
        });
      });

      it('teacher accesses dashboard CE 2019 with correct values', () => {
        cy.intercept('GET', '**/teachers/dashboards/executions/*').as(
          'getTeacherDashboard'
        );
        
        cy.demoTeacherLogin();

        cy.wait(500);
        cy.contains('Change course').click();
        cy.wait(500);
        cy.contains('div', '1st Semester 2019/2020').next('div').find('button').click();
    
        cy.wait(500);
        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getTeacherDashboard');
    
        cy.wait(500);
        cy.get('[data-cy="numAtLeast3Quizzes"]').should('contain',20); 
        cy.get('[data-cy="numMore75CorrectQuestions"]').should('contain',8); 
        cy.get('[data-cy="totalStudents"]').should('contain',3);
        
        cy.get('[data-cy="answeredQuestionsUnique"]').should('contain',80); 
        cy.get('[data-cy="averageQuestionsAnswered"]').should('contain',40); 
        cy.get('[data-cy="numAvailable"]').should('contain',120);
        
        cy.get('[data-cy="averageQuizzesSolved"]').should('contain',40); 
        cy.get('[data-cy="numQuizzes"]').should('contain',160); 
        cy.get('[data-cy="numUniqueAnsweredQuizzes"]').should('contain',70);
        
        cy.get('canvas').should('not.exist');

        cy.contains('Logout').click();

        Cypress.on('uncaught:exception', (err, runnable) => {
          // returning false here prevents Cypress from
          // failing the test
          return false;
        });
      });
    
    

    
});
  
