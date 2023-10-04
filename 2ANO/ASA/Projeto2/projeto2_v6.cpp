#include <iostream>
#include <vector>

using namespace std;



typedef vector<vector<int>> graph;
typedef vector<int> vector_int;

int valid=1;
int n,m,nodes[2];

graph g;
vector_int vis;
//vector_int res;

void parse_input(){
    cin >> n >> m;
    g=vector<vector<int>>(n,vector<int>());
    vis=vector<int>(n,0);
    for(int i=0;i<m;i++){
        int u,v;
        cin >> u >> v;
        g[v-1].push_back(u-1);
        if(g[v-1].size()>2){
            valid=0;
            break;
        }
    }
}

bool visit(int v){
        vis[v]=1;
        for(size_t a=0; a<g[v].size();a++){
            int v_a=g[v][a];
            if(vis[v_a]==0){
                if(visit(v_a))
                    return true;
            }
            else if(vis[v_a]==1)
                return true;
        }
        vis[v]=2;
        return false;
    }

    
    bool check_loop(){
        for(size_t i=0;i<g.size();i++){
            if(vis[i]==0){
                if(visit(i))
                    return true;
            }
        }
        return false;
    }

    void visit_dfs(int v, int i){
        if(i==0)
            vis[v]=3;
        else{
            vis[v]+=5;
        }
        for(size_t a=0;a<g[v].size();a++){
            int v_a=g[v][a];
            if((vis[v_a]<3 && i==0) || (vis[v_a]<5 && i==1)){
                visit_dfs(v_a,i);
            }
        }
        if(i==0)
            vis[v]=4;
    }

    void dfs(){
        for(size_t i=0;i<2;i++){
            if(vis[nodes[i]-1]!=4 && i==0)
                visit_dfs(nodes[i]-1,i);
            else if(vis[nodes[i]-1]!=6 && i==1)
                visit_dfs(nodes[i]-1,i);
        }
    }

int main(){
    ios_base::sync_with_stdio(false);
    cin >> nodes[0] >> nodes[1];
    parse_input();
    if(valid==0){
        printf("0\n");
    }
    else{
        if(check_loop())
            printf("0\n");
        else{
            dfs();        
            for(int i=0;i<n;i++){
                if(vis[i]>=9){
                    for(size_t j=0;j<g[i].size();j++)
                        vis[g[i][j]]++;
                }
            }
            for(int i=0;i<n;i++){
                if(vis[i]==9){
                    valid=0;
                    printf("%d ",i+1);
                }
            }
            if(valid)
                printf("-");
            printf("\n");
        }
    }
    return 0;
}