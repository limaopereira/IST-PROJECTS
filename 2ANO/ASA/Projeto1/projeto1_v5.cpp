#include <iostream>
#include <vector>
#include <sstream>
#include <utility>
#include <unordered_map>

using namespace std;


void prob1(vector<int> v){
    int lenV=v.size();
    int length=0;
    vector<vector<pair<int,int>>> tails(lenV);
    for(int i=0;i<lenV;i++){
        int l=0, r=length;
        while(l<r){
		    int m=l+(r-l)/2;
		    if(tails[m].back().first<v[i])
			    l=m+1;
		    else
			    r=m;
	    }
        if(l==length)
            length++;
        int count=1;
        if(l-1>=0){
            int l1=0, r1=tails[l-1].size();
            while(l1<r1){
		        int m=l1+(r1-l1)/2;
                if(tails[l-1][m].first<v[i])
                    r1=m;
                else
                    l1=m+1;
	        }
            count=tails[l-1].back().second;
            if(l1!=0)
                count-=tails[l-1][l1-1].second;      
        }
        if(tails[l].empty())
            tails[l].push_back({v[i],count});
        else
            tails[l].push_back({v[i],count+tails[l].back().second});
    }
    printf("%d %d\n",length,tails[length-1].back().second);
}

void prob2(unordered_map<int,vector<int>> v1, int lenV1){
    string line;
    int n,length=1;
    vector<vector<pair<int,int>>> tails(lenV1);
    getline(cin >> ws,line);
    istringstream iss(line);
    while(iss>>n){
        if(v1.find(n)!=v1.end()){
            if(tails[0].empty()){
                tails[0].push_back({n,v1[n][0]});
            }
            else{
                int i=v1[n][0],k=0,l=0,estado=0;
                for(k=length-1;k>=0 && estado==0;k--){
                    for(l=tails[k].size()-1;l>=0 && estado==0;l--){
                        if(n>tails[k][l].first){
                            for(size_t m=0;m<v1[n].size() && estado==0;m++){
                                if(v1[n][m]>tails[k][l].second){
                                    i=v1[n][m];
                                    estado=1;
                                }
                            }
                        }
                    }
                }
                if(estado){
                    if(k+1==length-1){
                        tails[length++].push_back({n,i});
                    }
                    else
                        tails[k+2].push_back({n,i});
                }
                else
                    tails[0].push_back({n,i});
            }
        }
    }
    printf("%d\n",length);
}


int main(){
	int n_problema;
	cin >> n_problema;	
	if(n_problema==1){
		string line;
		vector<int> v;
		int n;
		getline(cin >> ws,line);
		istringstream iss(line);
		while(iss >> n)
			v.push_back(n);
		prob1(v);
	}
	else if(n_problema==2){
        string line;
        unordered_map<int,vector<int>> v;
		int n,i=0;
		getline(cin >> ws,line);
		istringstream iss(line);
		while(iss >> n)
            v[n].push_back(i++);
        prob2(v,i);
	}
	return 0;
}