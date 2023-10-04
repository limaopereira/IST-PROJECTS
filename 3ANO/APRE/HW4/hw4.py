import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy.io.arff import loadarff
from sklearn.cluster import KMeans
from sklearn.decomposition import PCA
from sklearn.metrics import silhouette_score, cluster
from sklearn.feature_selection import VarianceThreshold
from sklearn.preprocessing import MinMaxScaler
                
                
def purity_score(y_true, y_pred):
        confusion_matrix = cluster.contingency_matrix(y_true,y_pred)
        return np.sum(np.amax(confusion_matrix, axis=0)) / np.sum(confusion_matrix) 

data = loadarff('pd_speech.arff')

df = pd.DataFrame(data[0])
df['class'] = df['class'].str.decode('utf-8')

X = df.drop('class',axis=1)
y = df['class']

scaler = MinMaxScaler().fit(X)
X_norm = scaler.transform(X)
df_X_norm = pd.DataFrame(X_norm,columns=X.columns)

kmeans_algo_0 = KMeans(n_clusters=3,random_state=0)
kmeans_algo_1 = KMeans(n_clusters=3, random_state=1)
kmeans_algo_2 = KMeans(n_clusters=3, random_state=2)

kmeans_model_0 = kmeans_algo_0.fit(X_norm)
kmeans_model_1 = kmeans_algo_1.fit(X_norm)
kmeans_model_2 = kmeans_algo_2.fit(X_norm)

y_pred_model_0 = kmeans_model_0.labels_
y_pred_model_1 = kmeans_model_1.labels_
y_pred_model_2 = kmeans_model_2.labels_

silhouette_model_0 = silhouette_score(X_norm, y_pred_model_0)
silhouette_model_1 = silhouette_score(X_norm, y_pred_model_1)
silhouette_model_2 = silhouette_score(X_norm, y_pred_model_2)

print("Silhouette (Random = 0) =", silhouette_model_0)
print("Silhouette (Random = 1) =", silhouette_model_1)
print("Silhouette (Random = 2) =", silhouette_model_2)

purity_model_0 = purity_score(y,y_pred_model_0)
purity_model_1 = purity_score(y,y_pred_model_1)
purity_model_2 = purity_score(y,y_pred_model_2)

print("Purity (Random = 0) =", purity_model_0)
print("Purity (Random = 1) =", purity_model_1)
print("Purity (Random = 2) =", purity_model_2)

selection = VarianceThreshold().fit(df_X_norm)
variances=selection.variances_
features = selection.feature_names_in_
d_feature_variance = {}
for i in range(len(features)):
        d_feature_variance[features[i]] = variances[i]

sort_d_feature_variance = sorted(d_feature_variance.items(), key=lambda x: x[1], reverse=True)
y_variable = df_X_norm[sort_d_feature_variance[0][0]]
x_variable = df_X_norm[sort_d_feature_variance[1][0]]

plt.figure(figsize=(14,5))
plt.subplot(121)
plt.scatter(x_variable,y_variable, c=y.map({'0':'green','1':'red'}))
plt.title("Original Parkinson Diagnoses")
plt.ylabel(sort_d_feature_variance[0][0])
plt.xlabel(sort_d_feature_variance[1][0])

plt.subplot(122)
plt.scatter(x_variable,y_variable,c=y_pred_model_0)
plt.title("Previously learned k=3 clusters (random=0)")
plt.ylabel(sort_d_feature_variance[0][0])
plt.xlabel(sort_d_feature_variance[1][0])
plt.show()

pca = PCA(svd_solver='full')
pca.fit(df_X_norm)

l_explained_variance = pca.explained_variance_ratio_
explained_variance = 0
n_components = 0
while explained_variance < 0.8:
        explained_variance += l_explained_variance[n_components]
        n_components+=1        

print("Number of principal components necessary to explain more than 80% of variability =", n_components)