import pandas as pd
import matplotlib.pyplot as plt
from scipy.io.arff import loadarff
from sklearn import metrics
from sklearn.feature_selection import mutual_info_classif
from sklearn.model_selection import train_test_split 
from sklearn.tree import DecisionTreeClassifier


def getData(dataFrame,scores, numberFeatures):
        features = []
        for i in range(numberFeatures):
                features.append(scores[i][0])
        return dataFrame[features]


data = loadarff('pd_speech.arff')

df = pd.DataFrame(data[0])
df['class'] = df['class'].str.decode('utf-8')


X = df.drop('class',axis=1)
y = df['class']

minfo = mutual_info_classif(X,y,random_state=1)


scores = {}

for i in range(len(minfo)):
        scores[X.columns.values[i]] = minfo[i]

sort_scores = sorted(scores.items(), key=lambda x: x[1], reverse=True)

features_options = [5,10,40,100,250,700]
training_accuracy = []
testing_accuracy = []
predictor = DecisionTreeClassifier(random_state=1)

for number_of_features in features_options:
        X_selected = getData(df,sort_scores, number_of_features)
        X_train, X_test, y_train, y_test = train_test_split(X_selected,y, stratify=y, train_size=0.7, random_state=1)
        predictor.fit(X_train, y_train)
        y_train_pred = predictor.predict(X_train)
        y_test_pred = predictor.predict(X_test)
        training_accuracy.append(round(metrics.accuracy_score(y_train,y_train_pred),2))
        testing_accuracy.append(round(metrics.accuracy_score(y_test, y_test_pred),2))

print(training_accuracy)
print(testing_accuracy)
plt.plot(features_options,training_accuracy, label="Training Accuracy", marker='o')
plt.plot(features_options,testing_accuracy, label="Testing Accuracy", marker='o')
plt.xlabel('Number of features')
plt.ylabel('Accuracy (%)')
plt.legend()
for a,b in zip(features_options, testing_accuracy): 
    plt.text(a+20, b+0.005, str(b),fontweight='bold')
plt.show()