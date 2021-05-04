// for https://laktak.github.io/js-graphy/

// this code is live, edit me!

// graph.add(function(x) { return x*x; });

function smin(a, b, k) {
    let h = clamp(0.5 + 0.5 * (a - b) / k, 0.0, 1.0);
    return mix(a, b, h) - k * h * (1.0 - h);
}

function clamp(v, min, max) {
    return Math.min(Math.max(v, min), max);
}

function mix(start, end, t) {
    return start * (1 - t) + end * t;
}


function cavityShape(x) {
    x = x * 1.5 * 1.5
    return (x * x - 2) / 0.4;

}


graph.add(cavityShape);

function craterFunction(x) {
    x = x * 1.5
    let cavityShape = ((x * 1.5) * (x * 1.5)) - 2.0;
    let a = (Math.abs(x) - 1 - 0.5);
    let rimShape = 0.5 * a * a;
    let floorShape = -0.4;
    return Math.max(Math.min(cavityShape, rimShape), floorShape) / -floorShape;
}

graph.add(craterFunction);

function craterFunctionSmooth(x) {
    x = x * 1.5

    let cavityShape = ((x * 1.5) * (x * 1.5)) - 2.0;
    let a = (Math.abs(x) - 1 - 0.5);
    let rimShape = 0.5 * a * a;
    let floorShape = -0.4;
    let s = 0.14;
    return smin(smin(cavityShape, rimShape, s / 2), floorShape, -s) / -floorShape;
}

graph.add(craterFunctionSmooth);



