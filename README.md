# Building a functional 3D ray tracer using physically based rendering techniques.

## Introduction

In the modern world we are constantly surrounded by realistic computer generated imagery. From incredibly realistic films with awe-inspiring graphics to real-time immersive video games which provide a temporary escape into virtual reality. Out of the top 10 grossing games of 2015, 7 are based on a virtual 3D environment<sup>[[3](#3)]</sup> and would have dedicated developers working on rendering systems. The selling point of these ventures is the depth of realism which they offer<sup>[[4](#4)]</sup>  and, in recent years, this has really been a major market pull for research into more efficient rendering techniques resulting in exciting developments in the field<sup>[[5](#5)]</sup>.

Behind the scenes, these renderers have a high variety of techniques they employ in order to capture the environment as we see it. They model the interaction of light rays with a whole host of materials with increasing degrees of accuracy. In this project, I plan to investigate how these techniques are used achieve results by building my own ray tracer from scratch and evaluating how well it can render compared to existing implementations.

## Project Overview 

There are two main categories that the majority of renderers fall into. The distinction is made between techniques relying on _rasterization_ vs _ray/path tracing_. The basic methodology for ray tracing is to trace a multitude of rays for each pixel of the resulting image to compute how much light arrives from that direction. This is done by simulating the physical properties of light; including reflection, refraction and the effect of different materials on incoming light e.g. metals vs. glass. However, even for small images, the light along _millions_ of rays has to be computed resulting in slow running times. The payback for all this effort is a photo-realistic output which naturally simulates realistic phenomenon such as soft shadows, global illumination and caustics # TODO: FIND CITATION#. 

In contrast, rasterization provides a faster approach by dealing directly with points and transformations. Rather than simulating the passage of light, the vertices in 3D are directly mapped into 2D screen space and, for points not on vertices, the rendering attributes are calculated by interpolating (taking a weighted average based on distance) between the vertices # TODO: FIND CITATION#. The advantage of this technique is that it applies the exact transformation applied to every point which can be efficiently implemented in modern computers which are armed with powerful GPU's (Graphical Processing Units) capable of applying the same transformation to thousands points in parallel. The downside of this method is that it gives very little in terms of expressing realistic light behaviour, reflections and translucency are incredibly difficult to model and light behaviour such as shadows and ambient lighting have to be manually added using heuristics which often don't produce realistic results.

As a result, rasterization is mainly used for real-time applications such as games and interactive animations where the speed of rasterization pays off, but ray tracing is the go-to method for pre-rendered material such as films, packaging and anything relying on realistic CGI # TODO: FIND CITATION # . In this project, I plan on making a ray tracer because I am interested in investigating how and to what degree physically based models can produce accurate photo-realistic images and a real-time application isn't necessary. In addition, at the moment it is difficult to apply the same kind of optimisation for ray tracing due to hardware limitations but this is a field of active research with some very promising results # TODO: INSERT CITATION HERE #. Therefore, with the advent of ever more powerful technology there is a high possibility that ray tracing could be made efficient enough for real-time purposes.

## Choice of Technology

In order to build the application, I need to first decide which programming language to use to build it in. The industry standard for rendering systems is using C++ since it provides a high degree of low level support which allows you to optimise efficient code, also C++ provides very little overhead meaning its memory usage is often far lower as well. However, for this project I have decided to use a different programming language called Scala. This language is more expressive and higher level meaning it would require less program code to be written than the equivalent in C++, so would require less development time. Another advantage is that it runs on the Java Virtual Machine (JVM), as a result the program, once compiled, can be run on any system supporting Java and the JVM is a highly optimised system which will provide a fast runtime (although not as fast as C++). 

An experimental ray tracer <sup>[[6](#6)]</sup> was run as a benchmark to compare the performances of languages. It showed that Scala is roughly 3 times slower than the same algorithm written in C++, but it used less than half the number of lines of code. For a large project with many developers C++ is a good choice, but in my case Scala's conciseness outweighs the relatively small performance loss. In addition, Scala is easier to maintain and debug therefore even more time would be saved in development when reasoning about the algorithms.

## References

##### 1 
###### Pharr, M., Humphreys, G. and Jakob, W. (2010) Physically based rendering: From theory to implementation - 2nd edition. 2nd edn. Amsterdam: Elsevier/Morgan Kaufmann Publishers.

##### 2 
###### Scratchapixel (no date) Available at: https://www.scratchapixel.com/ (Accessed: 8 January 2017).

##### 3
###### Romanyuk, S. (2016) Mobile Moba - a risky lane. Available at: http://www.wetapgame.com/2016/04/25/mobile-moba-risky-lane/ (Accessed: 8 January 2017).

##### 4
###### Low, G.S. (2001) ‘Understanding Realism in Computer Games through Phenomenology’, .

##### 5
###### Price, A. (2015) 24 Photorealistic blender renders. Available at: http://www.blenderguru.com/articles/24-photorealistic-blender-renders/ (Accessed: 8 January 2017).

##### 6
###### Ainsworth, H. (2013) < H X A 7 2 4 1 >: Minilight. Available at: http://www.hxa.name/minilight/ (Accessed: 10 January 2017).
