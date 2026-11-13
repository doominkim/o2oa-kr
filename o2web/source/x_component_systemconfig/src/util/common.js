if (window.NodeList && !NodeList.prototype.forEach) {
    NodeList.prototype.forEach = Array.prototype.forEach;
}
function _getRectPoint(rect){
    return {
        leftTop: {x: rect.x, y: rect.y},
        rightTop: {x: rect.x+rect.width, y: rect.y},
        leftBottom: {x: rect.x, y: rect.y+rect.height},
        rightBottom: {x: rect.x+rect.width, y: rect.y+rect.height},
    }
}
function _pointInRect(p, ps){
    return (p.x>=ps.leftTop.x && p.x<=ps.rightTop.x && p.y>=ps.leftTop.y && p.y<=ps.leftBottom.y)
}

function isOverlap(recta, rectb){
    const pa = _getRectPoint(recta);
    const pb = _getRectPoint(rectb);

    for (const k of ['leftTop', 'rightTop', 'leftBottom', 'rightBottom']){
        if (_pointInRect(pa[k], pb)) return true;
    }
    for (const k of ['leftTop', 'rightTop', 'leftBottom', 'rightBottom']){
        if (_pointInRect(pb[k], pa)) return true;
    }
    return false;
}

const downloadFile = (url, fileName = '') => {
    let eleLink = document.createElement('a');
    eleLink.download = fileName;
    eleLink.style.display = 'none';
    eleLink.target='_blank';
    eleLink.href = url;
    // 브라우저 보안 정책 때문에 동적으로 생성한 요소는 브라우저에 추가한 후에만 클릭할 수 있음
    document.body.appendChild(eleLink);
    // 클릭 실행  
    eleLink.click();
    // 그 후 제거
    document.body.removeChild(eleLink);
  };
  
const copyObject = (obj) => {
    return JSON.parse(JSON.stringify(obj));
}

export {isOverlap, downloadFile, copyObject};
