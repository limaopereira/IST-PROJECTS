import pandas as pd
import matplotlib.pyplot as plt
from scipy.io.arff import loadarff
from sklearn.model_selection import train_test_split 
from sklearn.linear_model import Ridge
from sklearn.neural_network import MLPRegressor
from sklearn.metrics import mean_absolute_error

data = loadarff('kin8nm.arff')
df = pd.DataFrame(data[0])
X = df.drop('y',axis=1)
y = df['y']
X_train, X_test, y_train, y_test = train_test_split(X, y,train_size=0.7, random_state=0)

ridge = Ridge(alpha=0.1)
ridge.fit(X_train.values,y_train)
y_test_pred_ridge = ridge.predict(X_test.values)
mae_ridge = mean_absolute_error(y_test, y_test_pred_ridge)
residues_ridge = y_test-y_test_pred_ridge

mlp1 = MLPRegressor(hidden_layer_sizes=(10,10,), activation='tanh', max_iter=500, early_stopping=True, random_state=0)
mlp1.fit(X_train.values,y_train)
y_test_pred_mlp1 = mlp1.predict(X_test.values)
mae_mlp1 = mean_absolute_error(y_test, y_test_pred_mlp1)
residues_mlp1 = y_test-y_test_pred_mlp1

mlp2 = MLPRegressor(hidden_layer_sizes=(10,10,), activation='tanh', max_iter=500, early_stopping=False, random_state=0)
mlp2.fit(X_train.values,y_train)
y_test_pred_mlp2 = mlp2.predict(X_test.values)
mae_mlp2 = mean_absolute_error(y_test, y_test_pred_mlp2)
residues_mlp2 = y_test-y_test_pred_mlp2

print("MAE Ridge", mae_ridge)
print("MAE MLP1", mae_mlp1)
print("MAE MLP2",mae_mlp2)

plt.hist(residues_ridge)
plt.xlabel('Residues (Absolute Value)')
plt.ylabel('Count')
plt.title('Ridge Residues')
plt.grid(True)
plt.show()

plt.hist(residues_mlp1)
plt.xlabel('Residues (Absolute Value)')
plt.ylabel('Count')
plt.title('MLP1 Residues')
plt.grid(True)
plt.show()

plt.hist(residues_mlp2)
plt.xlabel('Residues (Absolute Value)')
plt.ylabel('Count')
plt.title('MLP2 Residues')
plt.grid(True)
plt.show()

plt.boxplot([residues_ridge,residues_mlp1,residues_mlp2], labels=['Ridge','MLP1','MLP2'])
plt.ylabel('Residues (Absolute Value)')
plt.show()

print("MLP1 Iterations", mlp1.n_iter_)
print("MLP2 Iterations", mlp2.n_iter_)


