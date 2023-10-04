/////////////////////
/*   CONSTANTS     */
/////////////////////


// The constants defined here are mostly ASCII values for keyboard keys, 
// and some constants related to the movement and rotation of the ovni.

const ARROW_LEFT = "37";
const ARROW_UP = "38";
const ARROW_RIGHT = "39";
const ARROW_DOWN = "40";
const KEY_D = "68";
const KEY_E = "69";
const KEY_P = "80";
const KEY_Q = "81";
const KEY_V = "86";
const KEY_W = "87";
const KEY_R = "82";
const KEY_S = "83";
const KEY_1 = "49";
const KEY_2 = "50";


const OVNI_MOVE_UNIT = 25;
const OVNI_ROTATION_DEGREES = 100;

const keyHandlers = {
    [ARROW_LEFT]: () => {
        scene.ovni.moveX(-1);
        
    },
    [ARROW_UP]: () => {
        scene.ovni.moveZ(-1);
    },
    [ARROW_RIGHT]: () => {
        scene.ovni.moveX(1);
    },
    [ARROW_DOWN]: () => {
        scene.ovni.moveZ(1);
    },
    [KEY_D]: () => {scene.switchDirectionalLight(); keyCodes[KEY_D] = false;},
    [KEY_E]: () => {scene.changeMaterial(cartoon); keyCodes[KEY_E] = false;},
    [KEY_P]: () => {scene.switchPointLights(); keyCodes[KEY_P] = false;},
    [KEY_Q]: () => {scene.changeMaterial(gouraud); keyCodes[KEY_Q] = false;},
    [KEY_V]: () => {if(!in_vr)createScene(); keyCodes[KEY_V] = false;},
    [KEY_W]: () => {scene.changeMaterial(phong); keyCodes[KEY_W] = false;},
    [KEY_R]: () => {scene.switchLighting(); keyCodes[KEY_R] = false;},
    [KEY_S]: () => {scene.switchSpotLight(); keyCodes[KEY_S] = false;},
    [KEY_1]: () => {createFieldScene(); keyCodes[KEY_1] = false;},
    [KEY_2]: () => {createSkyScene(); keyCodes[KEY_2] = false;}, 
}

var sizes = {
    tree: {
        baseTrunk: {
            radius_top: 1,
            radius_bottom: 1,
            height: 10,
        },
        leaves: {
            radius: 4,
            x: 4,
            y: 1.5,
            z: 4,
            
        },
        topTrunk: {
            radius_top: 1,
            radius_bottom: 1,
            height: 6,
        },
    },
    ovni: {
        body: {
            radius:7,
            x: 7,
            y: 1.5,
            z: 7,
        },
        cockpit: {
            radius:3,
            x:3,
            y:3,
            z: 3,
        },
        spheric_light: {
            radius: 0.6,
            x: 0.6,
            y: 0.6,
            z: 0.6,
        },
        cilindrical_light: {
            radius_top: 2,
            radius_bottom: 2,
            height: 0.5,
        }
    },
    moon: {
        radius: 8,
        x: 8,
        y: 8,
        z: 8,
    },
    sky: {
        radius: 75,
        x: -75,
        y: 75,
        z: 75
    },
    house: {
        wall: {
            x: 13,
            y: 4,
            z: 7
        },
        door: {
            x: 2,
            y: 3,
            z: 0
        },
        window: {
            x: 2,
            y: 2,
            z: 0
        },
        roof: {
            x: 13,
            y: 2, 
            z: 7
        },
        chimney: {
            x: 3.5,
            y: 3,
            z: 1, 
        },
    }
}



//////////////////////
/* GLOBAL VARIABLES */
/////////////////////

// 'scene', 'fieldScene', 'skyScene', and 'activeScene' are variables for different scenes in the 3D environment. 

var scene, fieldScene, skyScene, activeScene;

// 'renderer' is used to render the scenes.

var renderer;

// 'clock' is an instance of THREE.Clock, which can be used to track the amount of time the program has been running.

var clock = new THREE.Clock();

// 'cameras' is an object that will store different camera configurations.

var cameras = {};

// 'activeCamera' will hold the currently active camera configuration.

var activeCamera;

// 'keyCodes' is an object that will store the state of keyboard keys (whether they are currently pressed or not).

var keyCodes = {};

// 'fieldTexture' and 'skyTexture' will hold the textures for the field and sky respectively.

var fieldTexture;
var skyTexture;

// 'in_vr' is a boolean variable to check if the VR mode is active.

var in_vr = false;

// 'dismap' and 'normap' will hold the displacement map and normal map respectively, which are used for adding detail to the textures.

var dismap;
var normap;

// 'basic', 'gouraud', 'phong', and 'cartoon' are objects that store different types of materials with various colors.
// These materials are used to color the 3D objects in the scenes.

// 'basic' uses THREE.MeshBasicMaterial which is not affected by lights.

var basic = {
    green : new THREE.MeshBasicMaterial({ color: 'green'}),
    brown : new THREE.MeshBasicMaterial({ color: 'brown'}),
    blue : new THREE.MeshBasicMaterial({ color: 'blue'}),
    grey : new THREE.MeshBasicMaterial({ color: 'grey'}),
    lightBlue : new THREE.MeshBasicMaterial({ color: 'lightblue'}),
    orange : new THREE.MeshBasicMaterial({ color: 'orange'}),
    white : new THREE.MeshBasicMaterial({ color: 'white'}),
    moonYellow: new THREE.MeshBasicMaterial({color: '#f0c420'}),
    red : new THREE.MeshBasicMaterial({ color: 'red'}),
    purple: new THREE.MeshBasicMaterial({color: 'purple'}),
    yellow: new THREE.MeshBasicMaterial({color: 'yellow'})
}

// 'gouraud' uses THREE.MeshLambertMaterial which is affected by lights and uses Gouraud shading (lighting calculations are done in the vertices).

var gouraud = {
    green : new THREE.MeshLambertMaterial({ color: 'green'}),
    brown : new THREE.MeshLambertMaterial({ color: 'brown'}),
    blue : new THREE.MeshLambertMaterial({ color: 'blue'}),
    grey : new THREE.MeshLambertMaterial({ color: 'grey'}),
    lightBlue : new THREE.MeshLambertMaterial({ color: 'lightblue'}),
    orange : new THREE.MeshLambertMaterial({ color: 'orange'}),
    white : new THREE.MeshLambertMaterial({ color: 'white'}),
    moonYellow: new THREE.MeshLambertMaterial({ color: '#f0c420',emissive: '#f0c420',emissiveIntensity:2}),
    red : new THREE.MeshLambertMaterial({ color: 'red'}),
}
// 'phong' uses THREE.MeshPhongMaterial which is affected by lights and uses Phong shading (lighting calculations are done in the pixels).

var phong = {
    green : new THREE.MeshPhongMaterial({ color: 'green'}),
    brown : new THREE.MeshPhongMaterial({ color: 'brown'}),
    blue : new THREE.MeshPhongMaterial({ color: 'blue'}),
    grey : new THREE.MeshPhongMaterial({ color: 'grey'}),
    lightBlue : new THREE.MeshPhongMaterial({ color: 'lightblue'}),
    orange : new THREE.MeshPhongMaterial({ color: 'orange'}),
    white : new THREE.MeshPhongMaterial({ color: 'white'}),
    moonYellow: new THREE.MeshPhongMaterial({ color: '#f0c420',emissive: '#f0c420',emissiveIntensity:2}),
    red : new THREE.MeshPhongMaterial({ color: 'red'}),
}

// 'cartoon' uses THREE.MeshToonMaterial which gives a cartoon-like appearance to the objects.

var cartoon = {
    green : new THREE.MeshToonMaterial({ color: 'green'}),
    brown : new THREE.MeshToonMaterial({ color: 'brown'}),
    blue : new THREE.MeshToonMaterial({ color: 'blue'}),
    grey : new THREE.MeshToonMaterial({ color: 'grey'}),
    lightBlue : new THREE.MeshToonMaterial({ color: 'lightblue'}),
    orange : new THREE.MeshToonMaterial({ color: 'orange'}),
    white : new THREE.MeshToonMaterial({ color: 'white'}),
    moonYellow: new THREE.MeshToonMaterial({ color: '#f0c420',emissive: '#f0c420',emissiveIntensity:2}),
    red : new THREE.MeshToonMaterial({ color: 'red'}),
}

/////////////////////
/* CREATE SCENE(S) */
/////////////////////

// 'createScene' function initializes the main scene and sets it as the active scene.

function createScene(){
    'use strict';
    scene = new Scene();    
    activeScene = scene;
    activeCamera = cameras.perspective;
}

// 'createFieldScene' function creates a scene with a field-like texture and random circles of different colors.

function createFieldScene(){
    fieldScene = new THREE.Scene();
    fieldScene.background = new THREE.Color('lightgreen');
    var width = 5;
    var height = 5; 
    const signal = [-1, 1];
    const colors = [basic.white, basic.yellow, basic.purple, basic.lightBlue];
    for (let i = 0; i < 100; i++) {
        var radius = Math.random() * 0.2;
        signal_x = signal[Math.floor(Math.random() * signal.length)];
        signal_y = signal[Math.floor(Math.random() * signal.length)];
        var x = signal_x*Math.random() * (width-radius);
        var y = signal_y*Math.random() * (height-radius);
        const geometry = new THREE.CircleGeometry( radius, 32 ); 
        const material = colors[Math.floor(Math.random() * colors.length)]  ; 
        const circle = new THREE.Mesh( geometry, material );
        circle.position.set(x,y,0);
        fieldScene.add(circle);
    }
    activeScene = fieldScene;
    activeCamera = cameras.texture;
}

// 'createSkyScene' function creates a scene with a sky-like texture and random stars.

function createSkyScene(){
    skyScene = new THREE.Scene();
    
    let canvas = document.createElement('canvas');
    canvas.width = 1024;
    canvas.height = 1024;
    let context = canvas.getContext('2d');

    context.clearRect(0, 0, canvas.width, canvas.height);

    var grd = context.createLinearGradient(0,0, 0, canvas.height);
    grd.addColorStop(0, 'darkblue');
    grd.addColorStop(1, 'darkviolet');
    context.fillStyle = grd;
    context.fillRect(0, 0, canvas.width, canvas.height);

    for (let i = 0; i < 5000; i++) {
        let x = Math.random() * canvas.width;
        let y = Math.random() * canvas.height;
        let radius = Math.random() * 1.5;
        context.beginPath();
        context.arc(x, y, radius, 0, Math.PI * 2);
        let colors = ['white'];
        context.fillStyle = colors[Math.floor(Math.random() * colors.length)];
        context.fill();
    }

    skyScene.background = new THREE.CanvasTexture(canvas);

    activeScene = skyScene;
    activeCamera = cameras.texture;
}


/////////////////////
/* CREATE MATERIAL */
/////////////////////


// 'createFielMaterials' function creates materials for the field with different shading techniques.
// It uses a custom shader for the basic material and built-in materials for the other shading techniques.
// The materials use a displacement map and a normal map for adding detail to the texture.

function createFielMaterials(dismap, normap){
    var vertexShader = `
        uniform sampler2D displacementMap;
        uniform float displacementScale;
        uniform vec2 repeat;
        varying vec2 vUv;
        void main() {
            vUv = uv * repeat;
            vec4 displacement = texture2D(displacementMap, vUv);
            vec3 newPosition = position + normal * displacementScale * displacement.x;
            gl_Position = projectionMatrix * modelViewMatrix * vec4(newPosition, 1.0);
        }
    `;

    var fragmentShader = `
        uniform sampler2D myTexture;
        varying vec2 vUv;
        void main() {
            gl_FragColor = texture2D(myTexture, vUv);
        }
    `;

    basic.fieldMaterial = new THREE.ShaderMaterial({
        uniforms: {
            displacementMap: { value: dismap },
            displacementScale: { value: 5 },
            myTexture: { value: fieldTexture.texture },
            repeat: {value: new THREE.Vector2(4, 4)},
        },
        vertexShader: vertexShader,
        fragmentShader: fragmentShader
    });

    gouraud.fieldMaterial = new THREE.MeshLambertMaterial({
        map: fieldTexture.texture,
        displacementMap: dismap,
        displacementScale: 5,
        normalMap: normap,
    }); 

    phong.fieldMaterial = new THREE.MeshPhongMaterial({
        map: fieldTexture.texture,
        displacementMap: dismap,
        displacementScale: 5,
        normalMap: normap,
    }); 

    cartoon.fieldMaterial = new THREE.MeshToonMaterial({
        map: fieldTexture.texture,
        displacementMap: dismap,
        displacementScale: 5,
        normalMap: normap,
    }); 

    gouraud.fieldMaterial.map.wrapS = THREE.RepeatWrapping;
    gouraud.fieldMaterial.map.wrapT = THREE.RepeatWrapping;
    
    phong.fieldMaterial.wrapS = THREE.RepeatWrapping;
    phong.fieldMaterial.map.wrapT = THREE.RepeatWrapping;

    cartoon.fieldMaterial.map.wrapS = THREE.RepeatWrapping;
    cartoon.fieldMaterial.map.wrapT = THREE.RepeatWrapping;

}

// 'createSkyMaterials' function creates materials for the sky with different shading techniques.
// The materials use a texture created from a canvas.

function createSkyMaterials(){
    basic.skyMaterial = new THREE.MeshBasicMaterial({map: skyTexture.texture});
    gouraud.skyMaterial = new THREE.MeshLambertMaterial({map: skyTexture.texture});
    phong.skyMaterial = new THREE.MeshPhongMaterial({map: skyTexture.texture});
    cartoon.skyMaterial = new THREE.MeshToonMaterial({map: skyTexture.texture});
}

//////////////////////
/* CREATE CAMERA(S) */
//////////////////////

// 'createPerspectiveCamera' function creates a perspective camera and sets it as the active camera.

function createPerspectiveCamera() {
    'use strict'
    cameras.perspective = new THREE.PerspectiveCamera(70,
                                         window.innerWidth / window.innerHeight,
                                         1,
                                         1000);
    cameras.perspective.position.set(50, 3, 15);
    cameras.perspective.lookAt(0,40,-30);
    activeCamera = cameras.perspective;
}

// 'createTextureCamera' function creates an orthographic camera that is used for the textures.

function createTextureCamera() {
    'use strict';
    
    cameras.texture = new THREE.OrthographicCamera(0.96 * -6,
                                            0.96 * 6 ,
                                            6,
                                            -6,
                                            0.1,
                                            1000);
    
    cameras.texture.position.set(0, 0, 10);                                    
    cameras.texture.lookAt(0,0,0);    
}

//////////////////////
/* CREATE TEXTURES */
//////////////////////

// 'createTexture' function creates a texture by rendering a scene to a WebGLRenderTarget.
// The texture is set to repeat based on the provided repeatS and repeatT parameters.

function createTexture( targetScene, repeatS, repeatT){
    
    var texture = new THREE.WebGLRenderTarget(512,512, { minFilter: THREE.LinearFilter, magFilter: THREE.NearestFilter});

    texture.texture.wrapS = THREE.RepeatWrapping;
    texture.texture.wrapT = THREE.RepeatWrapping;
    texture.texture.repeat.set(repeatS,repeatT);

    renderer.setRenderTarget(texture);
    renderer.clear();
    renderer.render(targetScene, cameras.texture);
    renderer.setRenderTarget(null);
    
    return texture;
}

////////////////////////
/* CREATE OBJECT3D(S) */
////////////////////////


/* -------------------------------------------------- */
/*                      GENERAL                       */
/* -------------------------------------------------- */

// 'createMesh' function creates a mesh of a specified type with a given size and material.
// It sets the position and rotation of the mesh based on the provided coordinates.

function createMesh(type, size, mat, x, y, z, r_x=0, r_y=0, r_z=0){
    var mesh;
    switch (type){
        case 'cube':
            mesh = createCube(size, mat)
            break;
        case 'cylinder':
            mesh = createCylinder(size, mat);
            break;
        case 'elipsoidal':
            mesh = createElipsoidal(size, mat);
            break;
        case 'halfSphere':
            mesh = createHalfSphere(size, mat);
            break;
    }
    mesh.position.add(new THREE.Vector3(x,y,z));
    mesh.rotation.x += r_x * Math.PI/180;
    mesh.rotation.y += r_y * Math.PI/180;
    mesh.rotation.z += r_z * Math.PI/180;
    return mesh;
}

// 'createCube' function creates a cube mesh with a given size and material.

function createCube(size, material){
    'use strict'
    var {x, y, z} = size;
    var geometry = new THREE.CubeGeometry(x, y, z);
    var mesh = new THREE.Mesh(geometry, material);
    return mesh;
}

// 'createCylinder' function creates a cylinder mesh with a given size and material. 

function createCylinder(size, material){
    'use strict'
    var {radius_top, radius_bottom, height} = size;
    var geometry = new THREE.CylinderGeometry(radius_top, radius_bottom, height);
    var mesh = new THREE.Mesh(geometry, material);
    return mesh;
}

// 'createElipsoidal' function creates an elipsoidal mesh with a given size and material.

function createElipsoidal(size, material){
    'use strict'
    var {radius, x, y, z} = size;
    var geometry = new THREE.SphereGeometry(radius, 32, 32);
    geometry.scale(x/radius, y/radius, z/radius);
    var mesh = new THREE.Mesh(geometry, material);
    return mesh;
}

// 'createHalfSphere' function creates an half sphere mesh with a given size and material.

function createHalfSphere(size, material){
   'use strict'
   var {radius, x, y, z} = size;
   var geometry = new THREE.SphereGeometry(radius, 32, 32, 0, Math.PI*2, 0, Math.PI/2);
   geometry.scale(x/radius, y/radius, z/radius);
   var mesh = new THREE.Mesh(geometry, material);
   return mesh;
}

//////////////////////
/*   CREATE TREE    */
//////////////////////

// 'Tree' class represents a tree object in the 3D scene.

class Tree extends THREE.Object3D {

    // The constructor initializes the tree and sets its position.
    // The 'scaleBaseTrunk' parameter is used to adjust the scale of the base trunk of the tree.
    constructor(x, y, z, scaleBaseTrunk) {
        super();
        this.baseTrunk = this.createBaseTrunk(scaleBaseTrunk);
        this.topTrunkLeft = this.createTopTrunk(scaleBaseTrunk,45);
        this.topTrunkRight = this.createTopTrunk(scaleBaseTrunk,-45);
        this.leaveLeft = this.createLeaveSide('left');
        this.leaveRight = this.createLeaveSide('right');
        this.leaveMiddle = this.createLeaveMiddle();
        this.position.set(x, y, z);
    }

    // 'createBaseTrunk' function creates the base trunk of the tree.
    // The 'scaleBaseTrunk' parameter is used to adjust the height of the base trunk.
    
    createBaseTrunk(scaleBaseTrunk){
        var baseTrunkSizes = {
            radius_top: sizes.tree.baseTrunk.radius_top,
            radius_bottom: sizes.tree.baseTrunk.radius_bottom,
            height: sizes.tree.baseTrunk.height * scaleBaseTrunk
        };
        var baseTrunk = createMesh('cylinder', baseTrunkSizes, phong.brown, 0, 0, 0, 0, 0, 0);
        this.add(baseTrunk);
        return baseTrunk;
    }

    // 'createTopTrunk' function creates the top trunk of the tree.
    // The 'scaleBaseTrunk' parameter is used to adjust the position of the top trunk relative to the base trunk.
    
    createTopTrunk(scaleBaseTrunk, degrees){
        var topTrunk = new THREE.Object3D();
        topTrunk.position.y += scaleBaseTrunk * sizes.tree.baseTrunk.height/2;
        topTrunk.add(createMesh('cylinder', sizes.tree.topTrunk, phong.brown, 0, sizes.tree.topTrunk.height/2, 0, 0, 0, 0));
        topTrunk.rotation.z += degrees * Math.PI/180;
        this.baseTrunk.add(topTrunk);
        return topTrunk;
    }

    // 'createLeaveSide' function creates the leaves on the side of the tree.
    
    createLeaveSide(side){
        var signal;
        var topTrunk;
        switch(side){
            case 'left':
                signal = -1;
                topTrunk = this.topTrunkLeft;
                break;
            case 'right':
                signal = 1;
                topTrunk = this.topTrunkRight;
                break;
        }
        var leave = createMesh('elipsoidal', sizes.tree.leaves, phong.green, -sizes.tree.topTrunk.radius_top*signal, sizes.tree.topTrunk.height, 0, 0, 0, signal*45)
        topTrunk.add(leave);
        return leave;
    }

    // 'createLeaveMiddle' function creates the leaves in the middle of the tree.
    
    createLeaveMiddle(){
        var leavesMiddle = createMesh('elipsoidal', sizes.tree.leaves, phong.green, sizes.tree.leaves.x, sizes.tree.leaves.y, 0, 0, 0, 0);
        this.leaveLeft.add(leavesMiddle);
        return leavesMiddle;
    }
    
    // 'changeMaterial' function changes the material of the tree.
    
    changeMaterial(materialType){
        this.leaveLeft.material = materialType.green;
        this.leaveRight.material = materialType.green;
        this.leaveMiddle.material = materialType.green;
        this.baseTrunk.material = materialType.brown;
        this.topTrunkLeft.children[0].material = materialType.brown;
        this.topTrunkRight.children[0].material = materialType.brown;
    }

}
/* -------------------------------------------------- */
/*                      HOUSE                         */
/* -------------------------------------------------- */

// 'House' class represents a house object in the 3D scene.

class House extends THREE.Mesh {

    // The constructor initializes the house and sets its position.
    
    constructor(x, y, z){
        super();
        this.vertices = this.createVertices(x, y, z);
        this.door = this.createHousePart(this.getIndexDoor(), phong.blue);
        this.windows = this.createHousePart(this.getIndexWindows(), phong.blue);
        this.wall = this.createHousePart(this.getIndexWall(), phong.white);
        this.chimney = this.createHousePart(this.getIndexChimney(), phong.white);
        this.roof = this.createHousePart(this.getIndexRoof(), phong.orange);
    }

    // 'createHousePart' function creates a part of the house with a given material.
    
    createHousePart(indexHousePart, material){
        const geometryHousePart = new THREE.BufferGeometry();
        
        geometryHousePart.setIndex(indexHousePart);
        geometryHousePart.setAttribute('position', this.vertices);

        var housePart = new THREE.Mesh(geometryHousePart, material);

        geometryHousePart.computeVertexNormals();
        this.add(housePart);
        return housePart;
    }
    
    // 'getIndexDoor' function return the indices of the vertices for the door.
    
    getIndexDoor(){
        const indexDoor = [
            0, 1, 2,
            0, 3, 1
        ];
        return indexDoor;
    }

    // 'getIndexWindows' function return the indices of the vertices for the windows.
    
    getIndexWindows(){
        const indexWindows = [
            7, 8, 9,
            8, 10, 9,
            18, 21, 23,
            21, 22, 23,
            26, 28 ,27,
            27, 28, 29
        ];
        return indexWindows;
    }

    // 'getIndexWall' function return the indices of the vertices for the wall.
    
    getIndexWall(){
        const indexWall = [
            2, 4 ,5,
            2, 5, 47,
            5, 7, 12,
            5, 12, 46,
            9, 10, 12,
            10, 13, 12,
            4, 14, 8,
            8, 14, 13,
            1, 16, 14,
            14, 16, 15,
            0, 16, 3,
            0, 17, 16,
            17, 19, 18,
            17, 48, 19,
            15, 23, 24,
            22, 24, 23,
            21, 25, 24,
            21, 26, 25,
            25, 27, 30,
            27, 29, 30,
            28, 49, 30,
            19, 49, 28,
            6, 11, 44,
            11, 43, 44, 
            20, 45, 31,
            31, 45, 42,
            42, 45, 44,
            42, 44, 43,
        ];
        return indexWall;
    }

    // 'getIndexChimney' function return the indices of the vertices for the chimney.
    
    getIndexChimney(){
        const indexChimney = [
            34, 36, 35,
            36, 37, 35,
            36, 38, 37,
            36, 39, 38,
            34, 35, 41,
            34, 41, 40,
            35, 37, 38,
            35, 38, 41,
            39, 40, 41,
            39, 41, 38
        ];
        return indexChimney;
    }

    // 'getIndexRoof' function return the indices of the vertices for the roof.
    
    getIndexRoof(){
        const indexRoof = [
            31, 33, 32,
            11, 31, 32,
            31, 42, 33,
            11, 32, 43,
            33, 42, 43,
            32, 33, 43, 
        ];
        return indexRoof;
    }

    // 'createVertices' function creates the vertices for the house.
    
    createVertices(x, y, z){
        var wallSizes = sizes.house.wall;
        var doorSizes = sizes.house.door;
        var windowSizes = sizes.house.window;
        var roofSizes = sizes.house.roof;
        var chimneySizes = sizes.house.chimney;

        const vertices = new Float32Array( [
            x, y, z + wallSizes.z/2,    // v0
            x - doorSizes.x, y + doorSizes.y, z + wallSizes.z/2,    // v1
            x - doorSizes.x, y, z + wallSizes.z/2,  // v2
            x, y + doorSizes.y, z + wallSizes.z/2,  // v3
            x - doorSizes.x, y + doorSizes.y/2, z + wallSizes.z/2,  // v4
            x - wallSizes.x/2, y + doorSizes.y/2, z + wallSizes.z/2,    // v5
            x - wallSizes.x/2, y, z + wallSizes.z/2,    // v6
            x - wallSizes.x/2 + windowSizes.x * 0.75, y + doorSizes.y/2, z + wallSizes.z/2, // v7 
            x - wallSizes.x/2 + windowSizes.x * 1.75, y + doorSizes.y/2, z + wallSizes.z/2, // v8
            x - wallSizes.x/2 + windowSizes.x * 0.75, y + doorSizes.y/2 + windowSizes.y, z + wallSizes.z/2, // v9
            x - wallSizes.x/2 + windowSizes.x * 1.75, y + doorSizes.y/2 + windowSizes.y, z + wallSizes.z/2, // v10
            x - wallSizes.x/2, y + wallSizes.y, z + wallSizes.z/2,  // v11
            x - wallSizes.x/2 + windowSizes.x * 0.75, y + wallSizes.y, z + wallSizes.z/2,   // v12
            x - wallSizes.x/2 + windowSizes.x * 1.75, y + wallSizes.y, z + wallSizes.z/2,   // v13
            x - doorSizes.x, y + wallSizes.y, z + wallSizes.z/2,    // v14 
            x + doorSizes.x/2, y + wallSizes.y, z + wallSizes.z/2,  // v15
            x + doorSizes.x/2, y + wallSizes.y * 0.75, z + wallSizes.z/2,                                                       // v16
            x + doorSizes.x/2, y, z + wallSizes.z/2,    // v17
            x + doorSizes.x/2, y + doorSizes.y/2, z + wallSizes.z/2,    // v18
            x + wallSizes.x/2, y + doorSizes.y/2, z + wallSizes.z/2,    // v19
            x + wallSizes.x/2, y, z + wallSizes.z/2,    // v20
            x + doorSizes.x/2 + windowSizes.x, y + doorSizes.y/2, z + wallSizes.z/2,    // v21
            x + doorSizes.x/2 + windowSizes.x, y + doorSizes.y/2 + windowSizes.y, z + wallSizes.z/2,    // v22
            x + doorSizes.x/2, y + doorSizes.y/2 + windowSizes.y, z + wallSizes.z/2,    // v23
            x + doorSizes.x/2 + windowSizes.x, y + wallSizes.y, z + wallSizes.z/2,  // v24
            x + doorSizes.x + windowSizes.x, y + wallSizes.y, z + wallSizes.z/2,    // v25
            x + doorSizes.x + windowSizes.x, y + doorSizes.y/2, z + wallSizes.z/2,  // v26
            x + doorSizes.x + windowSizes.x, y + doorSizes.y/2 + windowSizes.y, z + wallSizes.z/2,  // v27
            x + doorSizes.x + windowSizes.x*2, y + doorSizes.y/2, z + wallSizes.z/2,    // v28
            x + doorSizes.x + windowSizes.x*2, y + doorSizes.y/2 + windowSizes.y , z + wallSizes.z/2,   // v29
            x + doorSizes.x + windowSizes.x*2, y + wallSizes.y, z + wallSizes.z/2,  // v30
            x + wallSizes.x/2, y + wallSizes.y, z + wallSizes.z/2,  // v31
            x - wallSizes.x/2, y + wallSizes.y + roofSizes.y, z,    // v32
            x + wallSizes.x/2, y + wallSizes.y + roofSizes.y, z,    // v33
            x + doorSizes.x/2 + windowSizes.x/4, y + wallSizes.y, z + wallSizes.z/2,    // v34
            x + doorSizes.x/2 + windowSizes.x/4, y + wallSizes.y + chimneySizes.y, z + wallSizes.z/2,   // v35
            x + doorSizes.x/2 + windowSizes.x/4 + chimneySizes.x, y + wallSizes.y, z + wallSizes.z/2,   // v36
            x + doorSizes.x/2 + windowSizes.x/4 + chimneySizes.x, y + wallSizes.y + chimneySizes.y, z + wallSizes.z/2,  // v37
            x + doorSizes.x/2 + windowSizes.x/4 + chimneySizes.x, y + wallSizes.y + chimneySizes.y, z + wallSizes.z/2 - sizes.house.chimney.z,  // v38
            x + doorSizes.x/2 + windowSizes.x/4 + chimneySizes.x, y + wallSizes.y + (roofSizes.y/(wallSizes.z/2)), z + wallSizes.z/2 - sizes.house.chimney.z, // v39
            x + doorSizes.x/2 + windowSizes.x/4, y + wallSizes.y + (roofSizes.y/(wallSizes.z/2)), z + wallSizes.z/2 - sizes.house.chimney.z,  // v40
            x + doorSizes.x/2 + windowSizes.x/4, y + wallSizes.y + chimneySizes.y, z + wallSizes.z/2 - sizes.house.chimney.z,   // v41
            x + wallSizes.x/2, y + wallSizes.y, z - wallSizes.z/2,  // v42
            x - wallSizes.x/2, y + wallSizes.y, z - wallSizes.z/2,  // v43
            x - wallSizes.x/2, y, z - wallSizes.z/2,    // v44  
            x + wallSizes.x/2, y, z - wallSizes.z/2,    // v45
            x - wallSizes.x/2, y + wallSizes.y, z + wallSizes.z/2,  // v46 = v11
            x - wallSizes.x/2, y, z + wallSizes.z/2,    // v47 = v6
            x + wallSizes.x/2, y, z + wallSizes.z/2,    // v48 = v20
            x + wallSizes.x/2, y + wallSizes.y, z + wallSizes.z/2,  // v49 = v31
        ] );

        return new THREE.BufferAttribute(vertices, 3);
    }

    // 'changeMaterial' function changes the material of the house.
    
    changeMaterial(materialType){
        this.door.material = materialType.blue;
        this.windows.material = materialType.blue;
        this.wall.material = materialType.white;
        this.chimney.material = materialType.white;
        this.roof.material = materialType.orange;
    }
}
    

/* -------------------------------------------------- */
/*               CREATE OVNI                          */
/*--------------------------------------------------- */

// 'Ovni' class represents a UFO object in the 3D scene.

class Ovni extends THREE.Object3D{

    // The constructor initializes the UFO and sets its position.
    
    constructor(x,y,z){
        super();
        this.body = this.createBody();
        this.cockpit = this.createCockpit();
        this.spotLight = this.createSpotLight();
        this.pointLights = this.createPointLights();
        this.moveVector = new THREE.Vector3(0,0,0);
        this.position.set(x, y, z);
    }

    // 'moveX' function sets the UFO moveVector along the x-axis.
    
    moveX(x){
        this.moveVector.add(new THREE.Vector3(x,0,0));
    }
    
    // 'moveZ' function sets the UFO moveVector along the z-axis.
    
    moveZ(z){
        this.moveVector.add(new THREE.Vector3(0,0,z));
    }

    // 'move' function moves the UFO based on the move vector and rotates it.
    // The moveVector is normalized to get a unit vector. This is done because we want the UFO to move at a constant speed
    // in all directions. Without normalization, the UFO would move faster when moving diagonally.
    // The normalized vector is then scaled by the product of the time since the last frame (deltaTime) and the speed of the UFO (OVNI_MOVE_UNIT).
    // This ensures that the UFO moves at the correct speed and that the movement is smooth and frame rate independent.
    
    move(deltaTime){
        if(this.moveVector.x != 0 || this.moveVector.z != 0){
            this.position.add(
                this.moveVector
                    .normalize()
                    .multiplyScalar(deltaTime * OVNI_MOVE_UNIT)
            );
            this.moveVector.set(0,0,0);
        }
        this.rotation.y += (OVNI_ROTATION_DEGREES * Math.PI/180)*deltaTime;
    }

    // 'createBody' function creates the body of the UFO.
    
    createBody(){
        var body = createMesh('elipsoidal', sizes.ovni.body, phong.grey, 0, 0, 0, 0, 0, 0);
        this.add(body);
        return body;
    }

    // 'createCockpit' function creates the cockpit of the UFO.
    
    createCockpit(){
        var cockpit = createMesh('halfSphere', sizes.ovni.cockpit, phong.lightBlue, 0, sizes.ovni.body.y - 0.2, 0, 0, 0, 0);
        this.body.add(cockpit);
        return cockpit;
    }
    
    // 'createSpotLight' function creates the spotlight of the UFO.
    
    createSpotLight(){
        var cylinderLight = createMesh('cylinder', sizes.ovni.cilindrical_light, phong.blue, 0, -sizes.ovni.body.y - sizes.ovni.cilindrical_light.height/2 + 0.2, 0, 0, 0, 0); 
        this.body.add(cylinderLight);

        var targetObject = new THREE.Object3D();
        cylinderLight.add(targetObject);
        targetObject.position.set(0, -1, 0);

        var spotLight = new THREE.SpotLight('lightblue', 5 );
        spotLight.angle = Math.PI / 8;
        spotLight.penumbra = 1;
        spotLight.decay = 1;
        spotLight.target = targetObject;

        cylinderLight.add(spotLight);

        return cylinderLight;
    }

    // 'createPointLights' function creates the point lights of the UFO.
    
    createPointLights(){
        var sphericLights = new THREE.Object3D();

        var sphericLight1 = createMesh('elipsoidal', sizes.ovni.spheric_light,phong.red, (10/14)*sizes.ovni.body.radius, -sizes.ovni.body.y + sizes.ovni.spheric_light.radius, 0, 0, 0, 0);
        var sphericLight2 = createMesh('elipsoidal', sizes.ovni.spheric_light,phong.red, -(10/14)*sizes.ovni.body.radius, -sizes.ovni.body.y + sizes.ovni.spheric_light.radius, 0, 0, 0, 0);
        var sphericLight3 = createMesh('elipsoidal', sizes.ovni.spheric_light,phong.red, 0,-sizes.ovni.body.y + sizes.ovni.spheric_light.radius,(10/14)*sizes.ovni.body.radius, 0, 0, 0);
        var sphericLight4 = createMesh('elipsoidal', sizes.ovni.spheric_light,phong.red, 0, -sizes.ovni.body.y + sizes.ovni.spheric_light.radius,-(10/14)*sizes.ovni.body.radius, 0, 0, 0);
        
        sphericLights.add(sphericLight1);
        sphericLights.add(sphericLight2);
        sphericLights.add(sphericLight3);
        sphericLights.add(sphericLight4);        

        for(let i = 0;  i< sphericLights.children.length; i++){
            var pointLight = new THREE.PointLight('red', 0.2);
            pointLight.distance = 120;
            sphericLights.children[i].add(pointLight);
        }
       
        this.body.add(sphericLights);

        return sphericLights;
    }

    // 'changeMaterial' function changes the material of the UFO.
    
    changeMaterial(materialType){
        this.body.material = materialType.grey;
        this.cockpit.material = materialType.lightBlue;
        this.spotLight.material = materialType.blue;
        this.pointLights.traverse((pointLight) => pointLight.material = materialType.red);
        this.lighthing = 'on';
        this.materialName = materialType;
    }

    // 'switchSpotLight' function toggles the visibility of the spotlight.
    
    switchSpotLight(){
        this.spotLight.children.forEach(child => child.visible = !child.visible);
    }

    // 'switchPointLights' function toggles the visibility of the point lights.
    
    switchPointLights(){
        this.pointLights.children.forEach(
            sphericLight => sphericLight.children[0].visible = !sphericLight.children[0].visible
        );
    }

}

/* -------------------------------------------------- */
/*                  CREATE MOON                       */
/*--------------------------------------------------- */

// 'Moon' class represents a moon object in the 3D scene.

class Moon extends THREE.Object3D{

    // The constructor initializes the moon and sets its position.
    
    constructor(x,y,z){
        super();
        this.moon = this.createMoon();
        this.position.set(x, y, z);
    }
  
    // 'createMoon' function creates the moon.
    
    createMoon(){
        var moon = createMesh('elipsoidal', sizes.moon, phong.moonYellow, 0,0, 0, 0, 0, 0);
        this.add(moon);
        return moon;
    }

    // 'changeMaterial' function changes the material of the moon.
    
    changeMaterial(materialType){
        this.moon.material = materialType.moonYellow;
        this.lighthing = 'on';
        this.materialName = materialType;  
    }  
}

/* -------------------------------------------------- */
/*                  CREATE SKY                        */
/*--------------------------------------------------- */

// 'Sky' class represents a sky object in the 3D scene.

class Sky  extends THREE.Object3D{
    
    // The constructor initializes the sky and sets its position.
    
    constructor(x,y,z){
        super();
        this.sky = this.createSky();
        this.position.set(x, y, z);
    }

    // 'createSky' function creates the sky.
    
    createSky(){
        var sky = createMesh('halfSphere', sizes.sky, phong.skyMaterial, 0, 0, 0);
        this.add(sky);
        return sky;
        
    }

    // 'changeMaterial' function changes the material of the sky.
    
    changeMaterial(materialType){
        this.sky.material = materialType.skyMaterial;
        this.materialName = materialType;
    }  
}


/* -------------------------------------------------- */
/*                  CREATE FIELD                      */
/*--------------------------------------------------- */

// 'Field' class represents a field object in the 3D scene.

class Field  extends THREE.Object3D{

    // The constructor initializes the field and sets its position.
    
    constructor(x,y,z){
        super();
        this.field = this.createField();
        this.position.set(x, y, z);
    }

    // 'createField' function creates the field.
    
    createField(){
        var geometryField = new THREE.PlaneGeometry( 150, 150, 400, 400);    
        geometryField.rotateX(-Math.PI / 2);
        geometryField.rotateY(-3* Math.PI/ 2);

        var field = new THREE.Mesh(geometryField, phong.fieldMaterial);
        this.add(field);
        return field;
        
    }

    // 'changeMaterial' function changes the material of the field.
    
    changeMaterial(materialType){
        this.field.material = materialType.fieldMaterial;
        this.materialName = materialType;
    }
}

/* -------------------------------------------------- */
/*                  CREATE SCENE                      */
/*--------------------------------------------------- */

// 'Scene' class represents a scene in the 3D environment.

class Scene extends THREE.Scene{

    // The constructor initializes the scene and its components.
    
    constructor(){
        super();
        this.field = this.createField();
        this.sky = this.createSky();
        this.house = this.createHouse();
        this.trees = this.createTrees();
        this.ovni = this.createOvni();
        this.moon = this.createMoon();
        this.directionalLight = this.createDirectionalLight();
        this.ambientLight = this.createAmbientLight();
        this.materialType = phong; 
        this.lighthing = 'on';
    }

    // 'createField' function creates the field in the scene.
    
    createField(){
        fieldTexture = createTexture(fieldScene, 4, 4);
        createFielMaterials(dismap, normap);
        var field = new Field(0, 0, 0);
        this.add(field);
        return field;
    }

    // 'createSky' function creates the sky in the scene.
    
    createSky(){
        skyTexture = createTexture(skyScene, 1, 1);
        createSkyMaterials();
        var sky = new Sky(0,0,0);
        this.add(sky);
        return sky;
    }

    // 'createHouse' function creates the house in the scene.
    
    createHouse(){
        var house = new House(17, 2, -13);
        this.add(house);
        return house;
    }

    // The createTrees function generates a set of trees at specified positions.
    // Each tree is created with a random rotation and scale factor for variety.
    // The scale factor is applied to the base trunk of the tree, and the position of the base trunk is adjusted accordingly.
    
    createTrees(){
        var trees = new THREE.Object3D();
        var positions = [
            [43, 8, -9], [17, 5.3, -35], [5, 9.5, -50], [-10, 6, 0], 
            [0, 8.3, -10], [-10, 6, -35], [-50, 9, -22], [-40, 6.5, -40],
            [-30, 6.5, -22], [40, 7.7, -45], [32, 9, -20]
        ];
        positions.forEach(position => {
            var scaleBaseTrunk = Math.random()*(1-0.2)+0.2;
            var tree= new Tree(...position, scaleBaseTrunk);
            tree.baseTrunk.position.y -= (1-scaleBaseTrunk) * sizes.tree.baseTrunk.height/2;
            tree.rotation.y += Math.random()*2*Math.PI;
            trees.add(tree);
        });

        this.add(trees);
        return trees;
    }

    // 'createOvni' function creates the UFO in the scene.
    
    createOvni(){
        var ovni = new Ovni(-20, 40 -10);
        this.add(ovni);
        return ovni;
    }

    // 'createMoon' function creates the moon in the scene.
    
    createMoon(){
        var moon = new Moon(-40, 45, 15);
        this.add(moon);
        return moon;
    }
    
    // 'createDirectionalLight' function creates the directional light in the scene.
    
     createDirectionalLight(){
        var directionalLight = new THREE.DirectionalLight('#f0c420', 0.8)
        directionalLight.position.set(this.moon.position.x, this.moon.position.y, this.moon.position.z);
        this.add(directionalLight);

        return directionalLight;
    }

    // 'createAmbientLight' function creates the ambient light in the scene.
    
    createAmbientLight(){
        var ambientLight = new THREE.AmbientLight('#f0c420', 0.05);
        this.add(ambientLight);
        return ambientLight;
    }

    // 'changeMaterial' function changes the material of the scene components.
    
    changeMaterial(materialType){
        this.field.changeMaterial(materialType);
        this.sky.changeMaterial(materialType);
        this.house.changeMaterial(materialType);
        this.trees.children.forEach((tree) => tree.changeMaterial(materialType));
        this.ovni.changeMaterial(materialType);
        this.moon.changeMaterial(materialType);
        if(materialType != basic){
            this.materialType = materialType;
            this.lighthing = 'on';
        }
        else{
            this.lighthing = 'off';
        }
    }

    // 'switchDirectionalLight' function toggles the visibility of the directional light.
    
    switchDirectionalLight(){
        this.directionalLight.visible = !this.directionalLight.visible;
    }

    // 'switchSpotLight' function toggles the visibility of the UFO's spotlight.
    
    switchSpotLight(){
        this.ovni.switchSpotLight();
    }

    // 'switchPointLights' function toggles the visibility of the UFO's point lights.
    
    switchPointLights(){
        this.ovni.switchPointLights();
    }

    // 'switchLighting' function toggles the lighting of the scene.
    
    switchLighting(){
        if(this.lighthing=='on'){
            this.changeMaterial(basic);
        }
        else{
            this.changeMaterial(this.materialType);
            
        }
    }
}

////////////
/* UPDATE */
////////////

// The update function handles key presses and updates the animation.

function update(deltaTime){
    'use strict'

    for (const key in keyCodes) {
        if (keyCodes[key] && keyHandlers[key]) {
            keyHandlers[key]();
        }
    }

    scene.ovni.move(deltaTime);

}



/////////////
/* DISPLAY */
/////////////

// The render function renders the scene from the active camera's perspective.

function render() {
    'use strict';
    renderer.render(activeScene, activeCamera);
}


////////////////////////////////
/* INITIALIZE ANIMATION CYCLE */
////////////////////////////////

// The init function sets up the rendering context, creates the scene and cameras, and attaches event listeners for various user interactions.
// It also loads displacement and normal maps for the field texture, creates the field and sky scenes, and sets up VR rendering.

function init() {
    'use strict';
    renderer = new THREE.WebGLRenderer({
        antialias: true
    });
    renderer.setSize(window.innerWidth, window.innerHeight);
    
    document.body.appendChild(renderer.domElement);

    document.body.appendChild(VRButton.createButton(renderer));
    renderer.xr.enabled = true;

    dismap = new THREE.TextureLoader().load("maps/heightmap.png");
    dismap.wrapS = THREE.RepeatWrapping;
    dismap.wrapT = THREE.RepeatWrapping; 
    dismap.repeat.set(4,4);

    normap = new THREE.TextureLoader().load("maps/normalmap.png");
    normap.wrapS = THREE.RepeatWrapping;
    normap.wrapT = THREE.RepeatWrapping;
   
    createFieldScene();
    createSkyScene();

    createTextureCamera();
    
    createScene();
    createPerspectiveCamera();
    
    activeScene = skyScene;
    activeCamera = cameras.texture; 

    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("keyup", onKeyUp);
    window.addEventListener("resize", onResize);
    renderer.xr.addEventListener("sessionstart", onVRSessionStarted);
}


/////////////////////
/* ANIMATION CYCLE */
/////////////////////

// The animate function is the main loop of the application. It updates the scene and renders it continuously.
// It uses the time elapsed since the last frame to update the scene, and uses either requestAnimationFrame or setAnimationLoop for the loop, depending on whether VR mode is active.

function animate() {
    'use strict';
    var deltaTime = clock.getDelta();
    
    update(deltaTime);
    
    render();
    if(!in_vr){
        requestAnimationFrame(animate);
    }
    else{
        renderer.setAnimationLoop(animate);
    }
    
}

////////////////////////////
/* RESIZE WINDOW CALLBACK */
////////////////////////////

// The onResize function is an event handler that gets triggered when the window is resized. 
// It adjusts the aspect ratio of the renderer and the camera to match the new window size, 
// ensuring that the scene is displayed correctly regardless of the window size.

function onResize() { 
    'use strict';
    renderer.setSize(window.innerWidth, window.innerHeight);

    if (window.innerHeight > 0 && window.innerWidth > 0) {
        cameras.perspective.aspect = window.innerWidth / window.innerHeight;
        cameras.texture.aspect = window.innerWidth / window.innerHeight;
        cameras.perspective.updateProjectionMatrix();
        cameras.texture.updateProjectionMatrix();
    }

}


///////////////////////////////
/* VR SESSION START CALLBACK */
///////////////////////////////

// The onVRSessionStarted function is a callback that is triggered when a VR session starts.
// It sets the 'in_vr' flag to true and adjusts the scene's position to accommodate the VR view.
function onVRSessionStarted(){
    'use strict'
    activeScene = scene;
    in_vr = true;
	scene.position.set(-10, -5, -25);
}



///////////////////////
/* KEY DOWN CALLBACK */
///////////////////////

// The onKeyDown function is an event handler that gets triggered when a key is pressed. 
// It sets the corresponding key in the keyCodes object to true, indicating that the key is being pressed. 

function onKeyDown(e) {
    'use strict';
    keyCodes[e.keyCode] = true;
}

///////////////////////
/* KEY UP CALLBACK */
///////////////////////

// The onKeyUp function is an event handler that gets triggered when a key is released. 
// It sets the corresponding key in the keyCodes object to false, indicating that the key is no longer being pressed. 

function onKeyUp(e){
    'use strict';
    keyCodes[e.keyCode] = false;
}
