const {test}=require('node:test');
const assert=require('node:assert/strict');
const vm=require('node:vm');
const fs=require('node:fs');
function app(){
 const elements={},handlers={};
 const document={activeElement:null,getElementById:id=>elements[id],addEventListener:(t,f)=>handlers[t]=f};
 for(const id of ['hello','reset','status']){const events={};elements[id]={textContent:'',addEventListener:(t,f)=>events[t]=f,focus(){document.activeElement=this;},click(){events.click();}};}
 vm.runInNewContext(fs.readFileSync(require('node:path').join(__dirname,'../apps/hello-webos/js/app.js'),'utf8'),{document});
 return {elements,document,key(keyCode,repeat=false){let prevented=false;handlers.keydown({keyCode,repeat,preventDefault(){prevented=true;}});return prevented;}};
}
test('initial focus and all arrows wrap',()=>{const a=app();assert.equal(a.document.activeElement,a.elements.hello);for(const key of [37,38,39,40]){a.elements.hello.focus();assert.ok(a.key(key));assert.equal(a.document.activeElement,a.elements.reset);a.key(key);assert.equal(a.document.activeElement,a.elements.hello);}});
test('OK, repeat suppression, pointer and reset',()=>{const a=app();a.key(13);a.key(13,true);assert.match(a.elements.status.textContent,/1\./);a.elements.hello.click();assert.match(a.elements.status.textContent,/2\./);a.key(39);a.key(13);assert.match(a.elements.status.textContent,/Ready/);a.elements.hello.click();assert.match(a.elements.status.textContent,/1\./);});
test('Back stays with platform',()=>{const a=app();assert.equal(a.key(461),false);assert.equal(a.key(65),false);});
test('lost focus recovers',()=>{const a=app();a.document.activeElement=null;a.key(39);assert.equal(a.document.activeElement,a.elements.hello);});
