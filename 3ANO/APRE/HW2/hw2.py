import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from scipy import stats
from scipy.io.arff import loadarff
from sklearn.metrics import confusion_matrix, accuracy_score
from sklearn.neighbors import KNeighborsClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.model_selection import StratifiedKFold 
from sklearn.preprocessing import StandardScaler


data = loadarff('pd_speech.arff')

df = pd.DataFrame(data[0])
df['class'] = df['class'].str.decode('utf-8')

X = df.drop('class',axis=1)
y = df['class']

scaler = StandardScaler().fit(X)

knn_predictor = KNeighborsClassifier()
knn_cm_sum = np.array([[0,0],[0,0]])
knn_acc_scores = []

nb_predictor = GaussianNB()
nb_cm_sum = np.array([[0,0],[0,0]])
nb_acc_scores = []


folds = StratifiedKFold(n_splits=10, random_state=0, shuffle=True)

for train_k, test_k in folds.split(X,y):
        X_train, X_test = X.iloc[train_k], X.iloc[test_k]
        X_train, X_test = scaler.transform(X_train), scaler.transform(X_test)
        y_train, y_test = y.iloc[train_k], y.iloc[test_k]
        
        knn_predictor.fit(X_train,y_train)
        knn_y_pred = knn_predictor.predict(X_test)
        knn_cm = np.array(confusion_matrix(y_test,knn_y_pred,labels=['0','1']))
        knn_cm_sum = np.add(knn_cm_sum,knn_cm)
        knn_acc_scores.append(accuracy_score(y_test,knn_y_pred))
        
        nb_predictor.fit(X_train, y_train)
        nb_y_pred = nb_predictor.predict(X_test)
        nb_cm = np.array(confusion_matrix(y_test,nb_y_pred))
        nb_cm_sum = np.add(nb_cm_sum,nb_cm)
        nb_acc_scores.append(accuracy_score(y_test,nb_y_pred))

knn_cm_sum_dt = pd.DataFrame(knn_cm_sum, index=['Healthy', 'Parkinson Disease'], columns=['Predicted Healthy', ' Predicted Parkinson Disease'])
sns.heatmap(knn_cm_sum_dt,annot=True,fmt='g')
plt.title('kNN Cumulative Testing Confusion Matrix')
plt.show()

nb_cm_sum_dt = pd.DataFrame(nb_cm_sum, index=['Healthy', 'Parkinson Disease'], columns=['Predicted Healthy', ' Predicted Parkinson Disease'])
sns.heatmap(nb_cm_sum_dt,annot=True,fmt='g')
plt.title('Naive Bayes Cumulative Testing Confusion Matrix')
plt.show()

print('knn',round(np.mean(knn_acc_scores),2),'±',round(np.std(knn_acc_scores),2))
print('nb',round(np.mean(nb_acc_scores),2),'±',round(np.std(nb_acc_scores),2))

res = stats.ttest_rel(knn_acc_scores,nb_acc_scores,alternative='greater')
print(res.pvalue)