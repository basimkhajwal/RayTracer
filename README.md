# Building a functional 3D ray tracer using physically based rendering techniques.

## Introduction

In the modern world we are constantly surrounded by realistic computer generated imagery. From incredibly realistic films with awe-inspiring graphics to real-time immersive video games which provide a temporary escape into virtual reality. Out of the top 10 grossing games of 2015, 7 are based on a virtual 3D environment<sup>[[3](#3)]</sup> and would have dedicated developers working on rendering systems. The selling point of these ventures is the depth of realism which they offer<sup>[[4](#4)]</sup>  and, in recent years, this has really been a major market pull for research into more efficient rendering techniques resulting in exciting developments in the field<sup>[[5](#5)]</sup>.

Behind the scenes, these renderers have a high variety of techniques they employ in order to capture the environment as we see it. They model the interaction of light rays with a whole host of materials with increasing degrees of accuracy. In this project, I plan to investigate how these techniques are used achieve results by building my own ray tracer from scratch and evaluating how well it can render compared to existing implementations.

## Project Overview (incomplete)

- Describe about rasterization vs ray tracing
- Which algorithm to use for what and why
- Development of faster ray tracing methods in the future

There are two main formats that renderers follow. The distinction is made between techniques relying on _rasterization_ vs _ray/path tracing_. The basic methodology for ray tracing is to fire a ray for each pixel of the resulting image and compute how much light arrives from that direction, the main disadvantage of this method is that it is incredibly slow compared to rasterization, millions of rays have to fired. Rasterization provides a faster approach by dealing directly with triangles and transformations. The rendering is done by mapping 3D co-ordinates to a 2D section of the screen allowing for efficient rendering 

In order to build the application, I intend to use the programming language Scala. The advantage of this language being that it is very expressive, offers good library support and is cross platform therefore I can easily distribute it amongst different operating systems. However, the language comes at a performance cost compared to languages such as C/C++ which are the industry standard for rendering systems. The reason I did not choose to use these languages was because the development time would be much larger and the performance gain would only be measurable if considerable was spent in optimizing the lower level details of the program.

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
