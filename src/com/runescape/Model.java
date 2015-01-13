package com.runescape;

public class Model extends CacheLink {

	public static ModelContext[] contexts;

	public static Buffer obhead;
	public static Buffer obface1;
	public static Buffer obface2;
	public static Buffer obface3;
	public static Buffer obface4;
	public static Buffer obface5;
	public static Buffer obpoint1;
	public static Buffer obpoint2;
	public static Buffer obpoint3;
	public static Buffer obpoint4;
	public static Buffer obpoint5;
	public static Buffer obvertex1;
	public static Buffer obvertex2;
	public static Buffer obaxis;

	public static boolean[] verifyTriangleBounds = new boolean[2048];
	public static boolean[] projectTriangle = new boolean[2048];

	public static int[] vertexScreenX = new int[2048];
	public static int[] vertexScreenY = new int[2048];
	public static int[] vertexDepth = new int[2048];

	public static int[] projectSceneX = new int[2048];
	public static int[] projectSceneY = new int[2048];
	public static int[] projectSceneZ = new int[2048];

	public static int[] depthTriangleCount = new int[1500];
	public static int[][] depthTriangles = new int[1500][512];

	public static int[] priorityTriangleCount = new int[12];
	public static int[][] priorityTriangles = new int[12][2000];

	public static int[] normalTrianglePriority = new int[2000];
	public static int[] highTrianglePriority = new int[2000];
	public static int[] lowPriorityDepth = new int[12];

	public static int[] tmpX = new int[10];
	public static int[] tmpY = new int[10];
	public static int[] tmpColor = new int[10];

	public static int transformX;
	public static int transformY;
	public static int transformZ;

	public static boolean allowInput;
	public static int mouseX;
	public static int mouseY;

	public static int hoverCount;
	public static int[] hoveredBitsets = new int[1000];

	public static int[] sin = Canvas3D.sin;
	public static int[] cos = Canvas3D.cos;
	public static int[] palette = Canvas3D.palette;
	public static int[] zLerpArray = Canvas3D.zLerpArray;

	public int vertexCount;
	public int[] vertexX;
	public int[] vertexY;
	public int[] vertexZ;

	public int triangleCount;
	public int[] triangleVertexA;
	public int[] triangleVertexB;
	public int[] triangleVertexC;

	public int[] triangleColorA;
	public int[] triangleColorB;
	public int[] triangleColorC;

	public int[] triangleInfo;
	public int[] trianglePriority;
	public int[] triangleAlpha;

	public int[] unmodifiedTriangleColor;
	public int priority;

	public int texturedCount;
	public int[] textureVertexA;
	public int[] textureVertexB;
	public int[] textureVertexC;

	public int minBoundX;
	public int maxBoundX;
	public int maxBoundZ;
	public int minBoundZ;
	public int boundHeight;

	/**
	 * Seems inverted. Noticed a simularity in the landscape heightmap
	 */
	public int minBoundY, maxBoundY;

	public int maxDepth;
	public int minDepth;
	public int objectOffsetY;
	public int[] vertexLabel;
	public int[] triangleSkin;
	public int[][] labelVertices;
	public int[][] skinTriangle;
	public Normal[] normals;
	public Normal[] originalNormals;

	public static void unload() {
		contexts = null;
		obhead = null;
		obface1 = null;
		obface2 = null;
		obface3 = null;
		obface4 = null;
		obface5 = null;
		obpoint1 = null;
		obpoint2 = null;
		obpoint3 = null;
		obpoint4 = null;
		obpoint5 = null;
		obvertex1 = null;
		obvertex2 = null;
		obaxis = null;
		verifyTriangleBounds = null;
		projectTriangle = null;
		vertexScreenX = null;
		vertexScreenY = null;
		vertexDepth = null;
		projectSceneX = null;
		projectSceneY = null;
		projectSceneZ = null;
		depthTriangleCount = null;
		depthTriangles = null;
		priorityTriangleCount = null;
		priorityTriangles = null;
		normalTrianglePriority = null;
		highTrianglePriority = null;
		lowPriorityDepth = null;
		sin = null;
		cos = null;
		palette = null;
		zLerpArray = null;
	}

	public static void load(Archive a) {
		try {
			obhead = new Buffer(a.get("ob_head.dat"));
			obface1 = new Buffer(a.get("ob_face1.dat"));
			obface2 = new Buffer(a.get("ob_face2.dat"));
			obface3 = new Buffer(a.get("ob_face3.dat"));
			obface4 = new Buffer(a.get("ob_face4.dat"));
			obface5 = new Buffer(a.get("ob_face5.dat"));
			obpoint1 = new Buffer(a.get("ob_point1.dat"));
			obpoint2 = new Buffer(a.get("ob_point2.dat"));
			obpoint3 = new Buffer(a.get("ob_point3.dat"));
			obpoint4 = new Buffer(a.get("ob_point4.dat"));
			obpoint5 = new Buffer(a.get("ob_point5.dat"));
			obvertex1 = new Buffer(a.get("ob_vertex1.dat"));
			obvertex2 = new Buffer(a.get("ob_vertex2.dat"));
			obaxis = new Buffer(a.get("ob_axis.dat"));

			obhead.pos = 0;
			obpoint1.pos = 0;
			obpoint2.pos = 0;
			obpoint3.pos = 0;
			obpoint4.pos = 0;
			obvertex1.pos = 0;
			obvertex2.pos = 0;

			int count = obhead.readUShort();
			contexts = new ModelContext[count + 100];

			int vertexTextureDataOffset = 0;
			int labelDataOffset = 0;
			int triangleColorDataOffset = 0;
			int triangleInfoDataOffset = 0;
			int trianglePriorityDataOffset = 0;
			int triangleAlphaDataOffset = 0;
			int triangleSkinDataOffset = 0;

			for (int n = 0; n < count; n++) {
				int index = obhead.readUShort();
				ModelContext c = contexts[index] = new ModelContext();

				c.vertexCount = obhead.readUShort();
				c.triangleCount = obhead.readUShort();
				c.texturedCount = obhead.read();

				c.vertexFlagDataOffset = obpoint1.pos;
				c.vertexXDataOffset = obpoint2.pos;
				c.vertexYDataOffset = obpoint3.pos;
				c.vertexZDataOffset = obpoint4.pos;
				c.vertexIndexDataOffset = obvertex1.pos;
				c.triangleTypeDataOffset = obvertex2.pos;

				int hasInfo = obhead.read();
				int hasPriorities = obhead.read();
				int hasAlpha = obhead.read();
				int hasSkins = obhead.read();
				int hasLabels = obhead.read();

				for (int v = 0; v < c.vertexCount; v++) {
					int flags = obpoint1.read();

					if ((flags & 0x1) != 0) {
						obpoint2.readSmart();
					}

					if ((flags & 0x2) != 0) {
						obpoint3.readSmart();
					}

					if ((flags & 0x4) != 0) {
						obpoint4.readSmart();
					}
				}

				for (int t = 0; t < c.triangleCount; t++) {
					int type = obvertex2.read();

					if (type == 1) {
						obvertex1.readSmart();
						obvertex1.readSmart();
					}

					obvertex1.readSmart();
				}

				c.triangleColorDataOffset = triangleColorDataOffset;
				triangleColorDataOffset += c.triangleCount * 2;

				if (hasInfo == 1) {
					c.triangleInfoDataOffset = triangleInfoDataOffset;
					triangleInfoDataOffset += c.triangleCount;
				} else {
					c.triangleInfoDataOffset = -1;
				}

				if (hasPriorities == 255) {
					c.trianglePriorityDataOffset = trianglePriorityDataOffset;
					trianglePriorityDataOffset += c.triangleCount;
				} else {
					c.trianglePriorityDataOffset = -hasPriorities - 1;
				}

				if (hasAlpha == 1) {
					c.triangleAlphaDataOffset = triangleAlphaDataOffset;
					triangleAlphaDataOffset += c.triangleCount;
				} else {
					c.triangleAlphaDataOffset = -1;
				}

				if (hasSkins == 1) {
					c.triangleSkinDataOffset = triangleSkinDataOffset;
					triangleSkinDataOffset += c.triangleCount;
				} else {
					c.triangleSkinDataOffset = -1;
				}

				if (hasLabels == 1) {
					c.vertexLabelDataOffset = labelDataOffset;
					labelDataOffset += c.vertexCount;
				} else {
					c.vertexLabelDataOffset = -1;
				}

				c.triangleTextureDataOffset = vertexTextureDataOffset;
				vertexTextureDataOffset += c.texturedCount;
			}
		} catch (Exception e) {
			System.out.println("Error loading model index");
			e.printStackTrace();
		}
	}

	public Model() {
	}

	public Model(int index) {
		if (contexts != null) {
			ModelContext h = contexts[index];

			if (h == null) {
				System.out.println("Error model:" + index + " not found!");
			} else {
				vertexCount = h.vertexCount;
				triangleCount = h.triangleCount;
				texturedCount = h.texturedCount;

				vertexX = new int[vertexCount];
				vertexY = new int[vertexCount];
				vertexZ = new int[vertexCount];

				triangleVertexA = new int[triangleCount];
				triangleVertexB = new int[triangleCount];
				triangleVertexC = new int[triangleCount];

				textureVertexA = new int[texturedCount];
				textureVertexB = new int[texturedCount];
				textureVertexC = new int[texturedCount];

				if (h.vertexLabelDataOffset >= 0) {
					vertexLabel = new int[vertexCount];
				}

				if (h.triangleInfoDataOffset >= 0) {
					triangleInfo = new int[triangleCount];
				}

				if (h.trianglePriorityDataOffset >= 0) {
					trianglePriority = new int[triangleCount];
				} else {
					priority = -h.trianglePriorityDataOffset - 1;
				}

				if (h.triangleAlphaDataOffset >= 0) {
					triangleAlpha = new int[triangleCount];
				}

				if (h.triangleSkinDataOffset >= 0) {
					triangleSkin = new int[triangleCount];
				}

				unmodifiedTriangleColor = new int[triangleCount];

				obpoint1.pos = h.vertexFlagDataOffset;
				obpoint2.pos = h.vertexXDataOffset;
				obpoint3.pos = h.vertexYDataOffset;
				obpoint4.pos = h.vertexZDataOffset;
				obpoint5.pos = h.vertexLabelDataOffset;

				int x = 0;
				int y = 0;
				int z = 0;

				for (int v = 0; v < vertexCount; v++) {
					int flags = obpoint1.read();
					int x0 = 0, y0 = 0, z0 = 0;

					if ((flags & 0x1) != 0) {
						x0 = obpoint2.readSmart();
					}

					if ((flags & 0x2) != 0) {
						y0 = obpoint3.readSmart();
					}

					if ((flags & 0x4) != 0) {
						z0 = obpoint4.readSmart();
					}

					vertexX[v] = x + x0;
					vertexY[v] = y + y0;
					vertexZ[v] = z + z0;

					x = vertexX[v];
					y = vertexY[v];
					z = vertexZ[v];

					if (vertexLabel != null) {
						vertexLabel[v] = obpoint5.read();
					}
				}

				obface1.pos = h.triangleColorDataOffset;
				obface2.pos = h.triangleInfoDataOffset;
				obface3.pos = h.trianglePriorityDataOffset;
				obface4.pos = h.triangleAlphaDataOffset;
				obface5.pos = h.triangleSkinDataOffset;

				for (int n = 0; n < triangleCount; n++) {
					unmodifiedTriangleColor[n] = obface1.readUShort();

					if (triangleInfo != null) {
						triangleInfo[n] = obface2.read();
					}

					if (trianglePriority != null) {
						trianglePriority[n] = obface3.read();
					}

					if (triangleAlpha != null) {
						triangleAlpha[n] = obface4.read();
					}

					if (triangleSkin != null) {
						triangleSkin[n] = obface5.read();
					}
				}

				obvertex1.pos = h.vertexIndexDataOffset;
				obvertex2.pos = h.triangleTypeDataOffset;

				int a = 0;
				int b = 0;
				int c = 0;
				int last = 0;

				for (int n = 0; n < triangleCount; n++) {
					int type = obvertex2.read();

					if (type == 1) {
						a = obvertex1.readSmart() + last;
						last = a;

						b = obvertex1.readSmart() + last;
						last = b;

						c = obvertex1.readSmart() + last;
						last = c;

						triangleVertexA[n] = a;
						triangleVertexB[n] = b;
						triangleVertexC[n] = c;
					}

					if (type == 2) {
						b = c;
						c = obvertex1.readSmart() + last;
						last = c;

						triangleVertexA[n] = a;
						triangleVertexB[n] = b;
						triangleVertexC[n] = c;
					}

					if (type == 3) {
						a = c;
						c = obvertex1.readSmart() + last;
						last = c;

						triangleVertexA[n] = a;
						triangleVertexB[n] = b;
						triangleVertexC[n] = c;
					}

					if (type == 4) {
						int b0 = a;
						a = b;
						b = b0;
						c = obvertex1.readSmart() + last;
						last = c;

						triangleVertexA[n] = a;
						triangleVertexB[n] = b;
						triangleVertexC[n] = c;
					}
				}

				obaxis.pos = h.triangleTextureDataOffset * 6;

				for (int t = 0; t < texturedCount; t++) {
					textureVertexA[t] = obaxis.readUShort();
					textureVertexB[t] = obaxis.readUShort();
					textureVertexC[t] = obaxis.readUShort();
				}
			}
		}
	}

	public Model(Model[] models, int count) {
		boolean keepInfo = false;
		boolean keepPriorities = false;
		boolean keepAlpha = false;
		boolean keepSkins = false;

		vertexCount = 0;
		triangleCount = 0;
		texturedCount = 0;
		priority = -1;

		for (int n = 0; n < count; n++) {
			Model m = models[n];

			if (m != null) {
				vertexCount += m.vertexCount;
				triangleCount += m.triangleCount;
				texturedCount += m.texturedCount;
				keepInfo = keepInfo | m.triangleInfo != null;

				if (m.trianglePriority != null) {
					keepPriorities = true;
				} else {
					if (priority == -1) {
						priority = m.priority;
					}

					if (priority != m.priority) {
						keepPriorities = true;
					}
				}

				keepAlpha = keepAlpha | m.triangleAlpha != null;
				keepSkins = keepSkins | m.triangleSkin != null;
			}
		}

		vertexX = new int[vertexCount];
		vertexY = new int[vertexCount];
		vertexZ = new int[vertexCount];
		vertexLabel = new int[vertexCount];
		triangleVertexA = new int[triangleCount];
		triangleVertexB = new int[triangleCount];
		triangleVertexC = new int[triangleCount];
		textureVertexA = new int[texturedCount];
		textureVertexB = new int[texturedCount];
		textureVertexC = new int[texturedCount];

		if (keepInfo) {
			triangleInfo = new int[triangleCount];
		}

		if (keepPriorities) {
			trianglePriority = new int[triangleCount];
		}

		if (keepAlpha) {
			triangleAlpha = new int[triangleCount];
		}

		if (keepSkins) {
			triangleSkin = new int[triangleCount];
		}

		unmodifiedTriangleColor = new int[triangleCount];
		vertexCount = 0;
		triangleCount = 0;
		texturedCount = 0;

		for (int n = 0; n < count; n++) {
			Model m = models[n];

			if (m != null) {
				for (int t = 0; t < m.triangleCount; t++) {
					if (keepInfo) {
						if (m.triangleInfo == null) {
							triangleInfo[triangleCount] = 0;
						} else {
							triangleInfo[triangleCount] = m.triangleInfo[t];
						}
					}

					if (keepPriorities) {
						if (m.trianglePriority == null) {
							trianglePriority[triangleCount] = m.priority;
						} else {
							trianglePriority[triangleCount] = m.trianglePriority[t];
						}
					}

					if (keepAlpha) {
						if (m.triangleAlpha == null) {
							triangleAlpha[triangleCount] = 0;
						} else {
							triangleAlpha[triangleCount] = m.triangleAlpha[t];
						}
					}

					if (keepSkins && m.triangleSkin != null) {
						triangleSkin[triangleCount] = m.triangleSkin[t];
					}

					unmodifiedTriangleColor[triangleCount] = m.unmodifiedTriangleColor[t];
					triangleVertexA[triangleCount] = copyVertex(m, (m.triangleVertexA[t]));
					triangleVertexB[triangleCount] = copyVertex(m, (m.triangleVertexB[t]));
					triangleVertexC[triangleCount] = copyVertex(m, (m.triangleVertexC[t]));
					triangleCount++;
				}

				for (int t = 0; t < m.texturedCount; t++) {
					textureVertexA[texturedCount] = copyVertex(m, (m.textureVertexA[t]));
					textureVertexB[texturedCount] = copyVertex(m, (m.textureVertexB[t]));
					textureVertexC[texturedCount] = copyVertex(m, (m.textureVertexC[t]));
					texturedCount++;
				}
			}
		}
	}

	// unable to remove dummies because there would be conflicting constructors.
	// To fix this I could make a static method.
	public Model(Model[] models, int count, boolean dummy0, int dummy1) {
		boolean keepInfo = false;
		boolean keepPriorities = false;
		boolean keepAlpha = false;
		boolean keepColor = false;

		vertexCount = 0;
		triangleCount = 0;
		texturedCount = 0;
		priority = -1;

		for (int n = 0; n < count; n++) {
			Model m = models[n];

			if (m != null) {
				vertexCount += m.vertexCount;
				triangleCount += m.triangleCount;
				texturedCount += m.texturedCount;
				keepInfo = keepInfo | m.triangleInfo != null;

				if (m.trianglePriority != null) {
					keepPriorities = true;
				} else {
					if (priority == -1) {
						priority = m.priority;
					}
					if (priority != m.priority) {
						keepPriorities = true;
					}
				}

				keepAlpha = keepAlpha | m.triangleAlpha != null;
				keepColor = keepColor | m.unmodifiedTriangleColor != null;
			}
		}

		vertexX = new int[vertexCount];
		vertexY = new int[vertexCount];
		vertexZ = new int[vertexCount];

		triangleVertexA = new int[triangleCount];
		triangleVertexB = new int[triangleCount];
		triangleVertexC = new int[triangleCount];

		triangleColorA = new int[triangleCount];
		triangleColorB = new int[triangleCount];
		triangleColorC = new int[triangleCount];

		textureVertexA = new int[texturedCount];
		textureVertexB = new int[texturedCount];
		textureVertexC = new int[texturedCount];

		if (keepInfo) {
			triangleInfo = new int[triangleCount];
		}

		if (keepPriorities) {
			trianglePriority = new int[triangleCount];
		}

		if (keepAlpha) {
			triangleAlpha = new int[triangleCount];
		}

		if (keepColor) {
			unmodifiedTriangleColor = new int[triangleCount];
		}

		vertexCount = 0;
		triangleCount = 0;
		texturedCount = 0;

		for (int n = 0; n < count; n++) {
			Model m = models[n];

			if (m != null) {
				int lastVertex = vertexCount;

				for (int v = 0; v < m.vertexCount; v++) {
					vertexX[vertexCount] = m.vertexX[v];
					vertexY[vertexCount] = m.vertexY[v];
					vertexZ[vertexCount] = m.vertexZ[v];
					vertexCount++;
				}

				for (int t = 0; t < m.triangleCount; t++) {
					triangleVertexA[triangleCount] = m.triangleVertexA[t] + lastVertex;
					triangleVertexB[triangleCount] = m.triangleVertexB[t] + lastVertex;
					triangleVertexC[triangleCount] = m.triangleVertexC[t] + lastVertex;

					triangleColorA[triangleCount] = m.triangleColorA[t];
					triangleColorB[triangleCount] = m.triangleColorB[t];
					triangleColorC[triangleCount] = m.triangleColorC[t];

					if (keepInfo) {
						if (m.triangleInfo == null) {
							triangleInfo[triangleCount] = 0;
						} else {
							triangleInfo[triangleCount] = m.triangleInfo[t];
						}
					}

					if (keepPriorities) {
						if (m.trianglePriority == null) {
							trianglePriority[triangleCount] = m.priority;
						} else {
							trianglePriority[triangleCount] = m.trianglePriority[t];
						}
					}

					if (keepAlpha) {
						if (m.triangleAlpha == null) {
							triangleAlpha[triangleCount] = 0;
						} else {
							triangleAlpha[triangleCount] = m.triangleAlpha[t];
						}
					}

					if (keepColor && m.unmodifiedTriangleColor != null) {
						unmodifiedTriangleColor[triangleCount] = m.unmodifiedTriangleColor[t];
					}

					triangleCount++;
				}

				for (int t = 0; t < m.texturedCount; t++) {
					textureVertexA[texturedCount] = m.textureVertexA[t] + lastVertex;
					textureVertexB[texturedCount] = m.textureVertexB[t] + lastVertex;
					textureVertexC[texturedCount] = m.textureVertexC[t] + lastVertex;
					texturedCount++;
				}
			}
		}

		setupBBoxDepth();
	}

	public Model(Model from, boolean keepVertices, boolean keepColors, boolean keepAlpha, boolean keepInfo) {
		vertexCount = from.vertexCount;
		triangleCount = from.triangleCount;
		texturedCount = from.texturedCount;

		if (keepVertices) {
			vertexX = from.vertexX;
			vertexY = from.vertexY;
			vertexZ = from.vertexZ;
		} else {
			vertexX = new int[vertexCount];
			vertexY = new int[vertexCount];
			vertexZ = new int[vertexCount];
			for (int v = 0; v < vertexCount; v++) {
				vertexX[v] = from.vertexX[v];
				vertexY[v] = from.vertexY[v];
				vertexZ[v] = from.vertexZ[v];
			}
		}

		if (keepColors) {
			unmodifiedTriangleColor = from.unmodifiedTriangleColor;
		} else {
			unmodifiedTriangleColor = new int[triangleCount];
			for (int t = 0; t < triangleCount; t++) {
				unmodifiedTriangleColor[t] = from.unmodifiedTriangleColor[t];
			}
		}

		if (keepAlpha) {
			triangleAlpha = from.triangleAlpha;
		} else {
			triangleAlpha = new int[triangleCount];
			if (from.triangleAlpha == null) {
				for (int t = 0; t < triangleCount; t++) {
					triangleAlpha[t] = 0;
				}
			} else {
				for (int t = 0; t < triangleCount; t++) {
					triangleAlpha[t] = from.triangleAlpha[t];
				}
			}
		}

		if (keepInfo) {
			triangleInfo = from.triangleInfo;
		} else {
			triangleInfo = new int[triangleCount];

			if (from.triangleInfo == null) {
				for (int t = 0; t < triangleCount; t++) {
					triangleInfo[t] = 0;
				}
			} else {
				for (int t = 0; t < triangleCount; t++) {
					triangleInfo[t] = from.triangleInfo[t];
				}
			}
		}

		vertexLabel = from.vertexLabel;
		triangleSkin = from.triangleSkin;
		triangleVertexA = from.triangleVertexA;
		triangleVertexB = from.triangleVertexB;
		triangleVertexC = from.triangleVertexC;
		trianglePriority = from.trianglePriority;
		priority = from.priority;
		textureVertexA = from.textureVertexA;
		textureVertexB = from.textureVertexB;
		textureVertexC = from.textureVertexC;
	}

	public Model(Model m, boolean keepAlpha) {
		vertexCount = m.vertexCount;
		triangleCount = m.triangleCount;
		texturedCount = m.texturedCount;
		vertexX = new int[vertexCount];
		vertexY = new int[vertexCount];
		vertexZ = new int[vertexCount];

		for (int i = 0; i < vertexCount; i++) {
			vertexX[i] = m.vertexX[i];
			vertexY[i] = m.vertexY[i];
			vertexZ[i] = m.vertexZ[i];
		}

		if (keepAlpha) {
			triangleAlpha = m.triangleAlpha;
		} else {
			triangleAlpha = new int[triangleCount];
			if (m.triangleAlpha == null) {
				for (int i = 0; i < triangleCount; i++) {
					triangleAlpha[i] = 0;
				}
			} else {
				for (int i = 0; i < triangleCount; i++) {
					triangleAlpha[i] = m.triangleAlpha[i];
				}
			}
		}

		triangleInfo = m.triangleInfo;
		unmodifiedTriangleColor = m.unmodifiedTriangleColor;
		trianglePriority = m.trianglePriority;
		priority = m.priority;
		skinTriangle = m.skinTriangle;
		labelVertices = m.labelVertices;

		triangleVertexA = m.triangleVertexA;
		triangleVertexB = m.triangleVertexB;
		triangleVertexC = m.triangleVertexC;

		triangleColorA = m.triangleColorA;
		triangleColorB = m.triangleColorB;
		triangleColorC = m.triangleColorC;

		textureVertexA = m.textureVertexA;
		textureVertexB = m.textureVertexB;
		textureVertexC = m.textureVertexC;
	}

	private final int copyVertex(Model from, int i) {
		int selected = -1;
		int x = from.vertexX[i];
		int y = from.vertexY[i];
		int z = from.vertexZ[i];

		for (int v = 0; v < vertexCount; v++) {
			if (x == vertexX[v] && y == vertexY[v] && z == vertexZ[v]) {
				selected = v;
				break;
			}
		}

		if (selected == -1) {
			vertexX[vertexCount] = x;
			vertexY[vertexCount] = y;
			vertexZ[vertexCount] = z;

			if (from.vertexLabel != null) {
				vertexLabel[vertexCount] = from.vertexLabel[i];
			}

			selected = vertexCount++;
		}
		return selected;
	}

	public void setupBBoxDepth() {
		minBoundY = 0;
		boundHeight = 0;
		maxBoundY = 0;

		for (int v = 0; v < vertexCount; v++) {
			int x = vertexX[v];
			int y = vertexY[v];
			int z = vertexZ[v];

			if (-y > minBoundY) {
				minBoundY = -y;
			}

			if (y > maxBoundY) {
				maxBoundY = y;
			}

			int len = x * x + z * z;

			if (len > boundHeight) {
				boundHeight = len;
			}
		}

		boundHeight = (int) Math.sqrt((double) boundHeight);
		minDepth = (int) Math.sqrt((double) (boundHeight * boundHeight + minBoundY * minBoundY));
		maxDepth = minDepth + (int) Math.sqrt((double) (boundHeight * boundHeight + maxBoundY * maxBoundY));
	}

	public void setupBBox() {
		boundHeight = 0;

		minBoundX = 999999;
		maxBoundX = -999999;

		minBoundY = 0;
		maxBoundY = 0;

		maxBoundZ = -99999;
		minBoundZ = 99999;

		for (int v = 0; v < vertexCount; v++) {
			int x = vertexX[v];
			int y = vertexY[v];
			int z = vertexZ[v];

			if (x < minBoundX) {
				minBoundX = x;
			}

			if (x > maxBoundX) {
				maxBoundX = x;
			}

			if (z < minBoundZ) {
				minBoundZ = z;
			}

			if (z > maxBoundZ) {
				maxBoundZ = z;
			}

			if (-y > minBoundY) {
				minBoundY = -y;
			}

			if (y > maxBoundY) {
				maxBoundY = y;
			}

			int h = x * x + z * z;

			if (h > boundHeight) {
				boundHeight = h;
			}
		}

		boundHeight = (int) Math.sqrt((double) boundHeight);
		minDepth = (int) Math.sqrt((double) (boundHeight * boundHeight + minBoundY * minBoundY));
		maxDepth = minDepth + (int) Math.sqrt((double) (boundHeight * boundHeight + maxBoundY * maxBoundY));
	}

	public void applyGroups() {
		if (vertexLabel != null) {
			int[] labelCount = new int[256];
			int topLabel = 0;

			for (int v = 0; v < vertexCount; v++) {
				int lbl = vertexLabel[v];
				labelCount[lbl]++;

				if (lbl > topLabel) {
					topLabel = lbl;
				}
			}

			labelVertices = new int[topLabel + 1][];

			for (int l = 0; l <= topLabel; l++) {
				labelVertices[l] = new int[labelCount[l]];
				labelCount[l] = 0;
			}

			for (int v = 0; v < vertexCount; v++) {
				int lbl = vertexLabel[v];
				labelVertices[lbl][labelCount[lbl]++] = v;
			}

			vertexLabel = null;
		}

		if (triangleSkin != null) {
			int[] skinCount = new int[256];
			int topSkin = 0;

			for (int t = 0; t < triangleCount; t++) {
				int skin = triangleSkin[t];
				skinCount[skin]++;

				if (skin > topSkin) {
					topSkin = skin;
				}
			}

			skinTriangle = new int[topSkin + 1][];

			for (int s = 0; s <= topSkin; s++) {
				skinTriangle[s] = new int[skinCount[s]];
				skinCount[s] = 0;
			}

			for (int t = 0; t < triangleCount; t++) {
				int s = triangleSkin[t];
				skinTriangle[s][skinCount[s]++] = t;
			}

			triangleSkin = null;
		}
	}

	public void applyFrame(int frame) {
		if (labelVertices != null && frame != -1) {
			SeqFrame f = SeqFrame.instance[frame];
			SeqTransform t = f.transform;

			transformX = 0;
			transformY = 0;
			transformZ = 0;

			for (int n = 0; n < f.groupCount; n++) {
				int group = f.groups[n];
				transform(t.types[group], t.labels[group], f.x[n], f.y[n], f.z[n]);
			}
		}
	}

	public void applyFrames(int primaryFrame, int secondaryFrame, int[] labelGroups) {
		if (primaryFrame != -1) {
			if (labelGroups == null || secondaryFrame == -1) {
				applyFrame(primaryFrame);
			} else {
				SeqFrame primary = SeqFrame.instance[primaryFrame];
				SeqFrame secondary = SeqFrame.instance[secondaryFrame];
				SeqTransform t = primary.transform;

				transformX = 0;
				transformY = 0;
				transformZ = 0;

				int index = 0;
				int current = labelGroups[index++];

				for (int g = 0; g < primary.groupCount; g++) {
					int group;
					for (group = primary.groups[g]; group > current; current = labelGroups[index++]) {
						/* empty */
					}
					if (group != current || t.types[group] == 0) {
						transform(t.types[group], t.labels[group], primary.x[g], primary.y[g], primary.z[g]);
					}
				}

				transformX = 0;
				transformY = 0;
				transformZ = 0;

				index = 0;
				current = labelGroups[index++];

				for (int h = 0; h < secondary.groupCount; h++) {
					int group;

					for (group = secondary.groups[h]; group > current; current = labelGroups[index++]) {
						/* empty */
					}

					if (group == current || t.types[group] == 0) {
						transform(t.types[group], t.labels[group], secondary.x[h], secondary.y[h], secondary.z[h]);
					}
				}
			}
		}
	}

	private void transform(int type, int[] labels, int x, int y, int z) {
		int labelCount = labels.length;

		if (type == 0) {
			int i = 0; // vertex counter

			transformX = 0;
			transformY = 0;
			transformZ = 0;

			for (int n = 0; n < labelCount; n++) {
				int label = labels[n];

				if (label < labelVertices.length) {
					int[] vertices = labelVertices[label];

					for (int m = 0; m < vertices.length; m++) {
						int v = vertices[m];
						transformX += vertexX[v];
						transformY += vertexY[v];
						transformZ += vertexZ[v];
						i++;
					}
				}
			}

			if (i > 0) {
				// average transform x/y/z until we have the center of our label
				transformX = (transformX / i) + x;
				transformY = (transformY / i) + y;
				transformZ = (transformZ / i) + z;
			} else {
				transformX = x;
				transformY = y;
				transformZ = z;
			}
		} else if (type == 1) {
			for (int n = 0; n < labelCount; n++) {
				int label = labels[n];

				if (label < labelVertices.length) {
					int[] vertices = labelVertices[label];

					// simple translate
					for (int m = 0; m < vertices.length; m++) {
						int v = vertices[m];
						vertexX[v] += x;
						vertexY[v] += y;
						vertexZ[v] += z;
					}
				}
			}
		} else if (type == 2) {
			for (int n = 0; n < labelCount; n++) {
				int label = labels[n];

				if (label < labelVertices.length) {
					int[] vertices = labelVertices[label];
					for (int m = 0; m < vertices.length; m++) {
						int v = vertices[m];

						// translate to origin for rotation
						vertexX[v] -= transformX;
						vertexY[v] -= transformY;
						vertexZ[v] -= transformZ;

						int yaw = (x & 0xFF) * 8;
						int roll = (y & 0xFF) * 8;
						int pitch = (z & 0xFF) * 8;

						if (pitch != 0) {
							int s = sin[pitch];
							int c = cos[pitch];
							int x0 = ((vertexY[v] * s + vertexX[v] * c) >> 16);
							vertexY[v] = (vertexY[v] * c - vertexX[v] * s) >> 16;
							vertexX[v] = x0;
						}

						if (yaw != 0) {
							int s = sin[yaw];
							int c = cos[yaw];
							int y0 = ((vertexY[v] * c - vertexZ[v] * s) >> 16);
							vertexZ[v] = (vertexY[v] * s + vertexZ[v] * c) >> 16;
							vertexY[v] = y0;
						}

						if (roll != 0) {
							int s = sin[roll];
							int c = cos[roll];
							int x0 = ((vertexZ[v] * s + vertexX[v] * c) >> 16);
							vertexZ[v] = (vertexZ[v] * c - vertexX[v] * s) >> 16;
							vertexX[v] = x0;
						}

						// put back in position
						vertexX[v] += transformX;
						vertexY[v] += transformY;
						vertexZ[v] += transformZ;
					}
				}
			}
		} else if (type == 3) {
			for (int n = 0; n < labelCount; n++) {
				int l = labels[n];

				if (l < labelVertices.length) {
					int[] vertices = labelVertices[l];

					for (int m = 0; m < vertices.length; m++) {
						int v = vertices[m];

						// translate to origin for proper scaling
						vertexX[v] -= transformX;
						vertexY[v] -= transformY;
						vertexZ[v] -= transformZ;

						vertexX[v] = (vertexX[v] * x) / 128;
						vertexY[v] = (vertexY[v] * y) / 128;
						vertexZ[v] = (vertexZ[v] * z) / 128;

						// but back in place
						vertexX[v] += transformX;
						vertexY[v] += transformY;
						vertexZ[v] += transformZ;
					}
				}
			}
		} else if (type == 5) {
			if (skinTriangle != null && triangleAlpha != null) {
				for (int n = 0; n < labelCount; n++) {
					int l = labels[n];

					if (l < skinTriangle.length) {
						int[] triangles = skinTriangle[l];

						for (int m = 0; m < triangles.length; m++) {
							int t = triangles[m];

							triangleAlpha[t] += x * 8;

							if (triangleAlpha[t] < 0) {
								triangleAlpha[t] = 0;
							}

							if (triangleAlpha[t] > 255) {
								triangleAlpha[t] = 255;
							}
						}
					}
				}
			}
		}
	}

	public void rotateCounterClockwise() {
		for (int v = 0; v < vertexCount; v++) {
			int x = vertexX[v];
			vertexX[v] = vertexZ[v];
			vertexZ[v] = -x;
		}
	}

	public void rotatePitch(int angle) {
		int s = sin[angle];
		int c = cos[angle];
		for (int v = 0; v < vertexCount; v++) {
			int y = ((vertexY[v] * c - vertexZ[v] * s) >> 16);
			vertexZ[v] = (vertexY[v] * s + vertexZ[v] * c) >> 16;
			vertexY[v] = y;
		}
	}

	public void translate(int x, int y, int z) {
		for (int v = 0; v < vertexCount; v++) {
			vertexX[v] += x;
			vertexY[v] += y;
			vertexZ[v] += z;
		}
	}

	public void recolor(int from, int to) {
		for (int t = 0; t < triangleCount; t++) {
			if (unmodifiedTriangleColor[t] == from) {
				unmodifiedTriangleColor[t] = to;
			}
		}
	}

	public void invert() {
		for (int v = 0; v < vertexCount; v++) {
			vertexZ[v] = -vertexZ[v];
		}

		for (int t = 0; t < triangleCount; t++) {
			int a = triangleVertexA[t];
			triangleVertexA[t] = triangleVertexC[t];
			triangleVertexC[t] = a;
		}
	}

	public void scale(int x, int y, int z) {
		for (int v = 0; v < vertexCount; v++) {
			vertexX[v] = (vertexX[v] * x) / 128;
			vertexY[v] = (vertexY[v] * y) / 128;
			vertexZ[v] = (vertexZ[v] * z) / 128;
		}
	}

	public final void applyLighting(int baseLightness, int intensity, int x, int y, int z, boolean calculateLighting) {
		int light_length = (int) Math.sqrt((double) (x * x + y * y + z * z));
		int lightIntensity = intensity * light_length >> 8;

		if (triangleColorA == null) {
			triangleColorA = new int[triangleCount];
			triangleColorB = new int[triangleCount];
			triangleColorC = new int[triangleCount];
		}

		if (normals == null) {
			normals = new Normal[vertexCount];

			for (int v = 0; v < vertexCount; v++) {
				normals[v] = new Normal();
			}
		}

		for (int t = 0; t < triangleCount; t++) {
			int a = triangleVertexA[t];
			int b = triangleVertexB[t];
			int c = triangleVertexC[t];

			int deltaBAX = vertexX[b] - vertexX[a];
			int deltaBAY = vertexY[b] - vertexY[a];
			int deltaBAZ = vertexZ[b] - vertexZ[a];

			int deltaCAX = vertexX[c] - vertexX[a];
			int deltaCAY = vertexY[c] - vertexY[a];
			int deltaCAZ = vertexZ[c] - vertexZ[a];

			// crummy naming. They're just the differences between each point of
			// the triangle.
			int x0 = deltaBAY * deltaCAZ - deltaCAY * deltaBAZ;
			int y0 = deltaBAZ * deltaCAX - deltaCAZ * deltaBAX;
			int z0 = deltaBAX * deltaCAY - deltaCAX * deltaBAY;

			// while it's too large, shrink it by half
			for (; (x0 > 8192 || y0 > 8192 || z0 > 8192 || x0 < -8192 || y0 < -8192 || z0 < -8192);) {
				x0 >>= 1;
				y0 >>= 1;
				z0 >>= 1;
			}

			int length = (int) Math.sqrt((double) (x0 * x0 + y0 * y0 + z0 * z0));

			if (length <= 0) {
				length = 1;
			}

			// normalizing
			x0 = x0 * 256 / length;
			y0 = y0 * 256 / length;
			z0 = z0 * 256 / length;

			if (triangleInfo == null || (triangleInfo[t] & 0x1) == 0) {
				Normal v = normals[a];
				v.x += x0;
				v.y += y0;
				v.z += z0;
				v.magnitude++;

				v = normals[b];
				v.x += x0;
				v.y += y0;
				v.z += z0;
				v.magnitude++;

				v = normals[c];
				v.x += x0;
				v.y += y0;
				v.z += z0;
				v.magnitude++;
			} else {
				int lightness = baseLightness + (x * x0 + y * y0 + z * z0) / (lightIntensity + lightIntensity / 2);
				triangleColorA[t] = adjustHSLLightness(unmodifiedTriangleColor[t], lightness, triangleInfo[t]);
			}
		}

		if (calculateLighting) {
			calculateLighting(baseLightness, lightIntensity, x, y, z);
		} else {
			originalNormals = new Normal[vertexCount];

			for (int v = 0; v < vertexCount; v++) {
				Normal current = normals[v];
				Normal original = originalNormals[v] = new Normal();
				original.x = current.x;
				original.y = current.y;
				original.z = current.z;
				original.magnitude = current.magnitude;
			}
		}

		if (calculateLighting) {
			setupBBoxDepth();
		} else {
			setupBBox();
		}
	}

	public final void calculateLighting(int minLightness, int intensity, int x, int y, int z) {
		for (int t = 0; t < triangleCount; t++) {
			int a = triangleVertexA[t];
			int b = triangleVertexB[t];
			int c = triangleVertexC[t];

			if (triangleInfo == null) {
				int color = unmodifiedTriangleColor[t];

				Normal n = normals[a];
				int lightness = minLightness + ((x * n.x + y * n.y + z * n.z) / (intensity * n.magnitude));
				triangleColorA[t] = adjustHSLLightness(color, lightness, 0);

				n = normals[b];
				lightness = minLightness + ((x * n.x + y * n.y + z * n.z) / (intensity * n.magnitude));
				triangleColorB[t] = adjustHSLLightness(color, lightness, 0);

				n = normals[c];
				lightness = minLightness + ((x * n.x + y * n.y + z * n.z) / (intensity * n.magnitude));
				triangleColorC[t] = adjustHSLLightness(color, lightness, 0);
			} else if ((triangleInfo[t] & 0x1) == 0) {
				int color = unmodifiedTriangleColor[t];
				int info = triangleInfo[t];

				Normal v = normals[a];
				int lightness = minLightness + ((x * v.x + y * v.y + z * v.z) / (intensity * v.magnitude));
				triangleColorA[t] = adjustHSLLightness(color, lightness, info);

				v = normals[b];
				lightness = minLightness + ((x * v.x + y * v.y + z * v.z) / (intensity * v.magnitude));
				triangleColorB[t] = adjustHSLLightness(color, lightness, info);

				v = normals[c];
				lightness = minLightness + ((x * v.x + y * v.y + z * v.z) / (intensity * v.magnitude));
				triangleColorC[t] = adjustHSLLightness(color, lightness, info);
			}
		}

		normals = null;
		originalNormals = null;
		vertexLabel = null;
		triangleSkin = null;

		if (triangleInfo != null) {
			for (int t = 0; t < triangleCount; t++) {
				if ((triangleInfo[t] & 0x2) == 2) {
					return;
				}
			}
		}

		unmodifiedTriangleColor = null;
	}

	public static final int adjustHSLLightness(int hsl, int lightness, int type) {
		if ((type & 0x2) == 2) {
			if (lightness < 0) {
				lightness = 0;
			} else if (lightness > 127) {
				lightness = 127;
			}
			lightness = 127 - lightness;
			return lightness;
		}

		lightness = lightness * (hsl & 0x7f) >> 7;

		if (lightness < 2) {
			lightness = 2;
		} else if (lightness > 126) {
			lightness = 126;
		}

		return (hsl & 0xff80) + lightness;
	}

	public final void draw(int pitch, int yaw, int roll, int cameraX, int cameraY, int cameraZ, int cameraPitch) {
		final int centerX = Canvas3D.centerX;
		final int centerY = Canvas3D.centerY;

		int pitchsin = sin[pitch];
		int pitchcos = cos[pitch];

		int yawsin = sin[yaw];
		int yawcos = cos[yaw];

		int rollsin = sin[roll];
		int rollcos = cos[roll];

		int cpitchsin = sin[cameraPitch];
		int cpitchcos = cos[cameraPitch];

		int depth = cameraY * cpitchsin + cameraZ * cpitchcos >> 16;

		for (int v = 0; v < vertexCount; v++) {
			int x = vertexX[v];
			int y = vertexY[v];
			int z = vertexZ[v];

			if (roll != 0) {
				int z0 = y * rollsin + x * rollcos >> 16;
				y = y * rollcos - x * rollsin >> 16;
				x = z0;
			}

			if (pitch != 0) {
				int x0 = y * pitchcos - z * pitchsin >> 16;
				z = y * pitchsin + z * pitchcos >> 16;
				y = x0;
			}

			if (yaw != 0) {
				int y0 = z * yawsin + x * yawcos >> 16;
				z = z * yawcos - x * yawsin >> 16;
				x = y0;
			}

			x += cameraX;
			y += cameraY;
			z += cameraZ;

			int x0 = y * cpitchcos - z * cpitchsin >> 16;
			z = y * cpitchsin + z * cpitchcos >> 16;
			y = x0;

			vertexDepth[v] = z - depth;
			vertexScreenX[v] = centerX + (x << 9) / z;
			vertexScreenY[v] = centerY + (y << 9) / z;

			if (texturedCount > 0) {
				projectSceneX[v] = x;
				projectSceneY[v] = y;
				projectSceneZ[v] = z;
			}
		}
		draw(0, false, false);
	}

	public final void draw(int yaw, int cameraPitchSine, int cameraPitchCosine, int cameraYawSine, int cameraYawCosine, int sceneX, int sceneY, int sceneZ, int bitset) {
		int cameraY = sceneZ * cameraYawCosine - sceneX * cameraYawSine >> 16;
		int farZ = sceneY * cameraPitchSine + cameraY * cameraPitchCosine >> 16;
		int distanceZ = boundHeight * cameraPitchCosine >> 16;
		int nearZ = farZ + distanceZ;

		if (nearZ > Scene.NEAR_Z && farZ < Scene.FAR_Z) {
			int i_254_ = sceneZ * cameraYawSine + sceneX * cameraYawCosine >> 16;
			int minX = i_254_ - boundHeight << 9;

			if (minX / nearZ < Canvas2D.centerX) {
				int maxX = i_254_ + boundHeight << 9;

				if (maxX / nearZ > -Canvas2D.centerX) {
					int i_257_ = sceneY * cameraPitchCosine - cameraY * cameraPitchSine >> 16;
					int i_258_ = boundHeight * cameraPitchSine >> 16;
					int maxY = i_257_ + i_258_ << 9;

					if (maxY / nearZ > -Canvas2D.centerY) {
						int i_260_ = i_258_ + (minBoundY * cameraPitchCosine >> 16);
						int minY = i_257_ - i_260_ << 9;

						if (minY / nearZ < Canvas2D.centerY) {
							int i_262_ = distanceZ + (minBoundY * cameraPitchSine >> 16);
							boolean project = false;

							if (farZ - i_262_ <= Scene.NEAR_Z) {
								project = true;
							}

							boolean hasInput = false;

							if (bitset > 0 && allowInput) {
								int zd = farZ - distanceZ;

								if (zd <= Scene.NEAR_Z) {
									zd = Scene.NEAR_Z;
								}

								if (i_254_ > 0) {
									minX /= nearZ;
									maxX /= zd;
								} else {
									maxX /= nearZ;
									minX /= zd;
								}

								if (i_257_ > 0) {
									minY /= nearZ;
									maxY /= zd;
								} else {
									maxY /= nearZ;
									minY /= zd;
								}

								int x = mouseX - Canvas3D.centerX;
								int y = mouseY - Canvas3D.centerY;

								if (x > minX && x < maxX && y > minY && y < maxY) {
									hasInput = true;
								}
							}

							int cx = Canvas3D.centerX;
							int cy = Canvas3D.centerY;

							int yawsin = 0;
							int yawcos = 0;

							if (yaw != 0) {
								yawsin = sin[yaw];
								yawcos = cos[yaw];
							}

							for (int v = 0; v < vertexCount; v++) {
								int x = vertexX[v];
								int y = vertexY[v];
								int z = vertexZ[v];

								if (yaw != 0) {
									int w = z * yawsin + x * yawcos >> 16;
									z = z * yawcos - x * yawsin >> 16;
									x = w;
								}

								x += sceneX;
								y += sceneY;
								z += sceneZ;

								int w = z * cameraYawSine + x * cameraYawCosine >> 16;
								z = z * cameraYawCosine - x * cameraYawSine >> 16;
								x = w;

								w = y * cameraPitchCosine - z * cameraPitchSine >> 16;
								z = y * cameraPitchSine + z * cameraPitchCosine >> 16;
								y = w;

								vertexDepth[v] = z - farZ;

								if (z >= Scene.NEAR_Z) {
									vertexScreenX[v] = cx + (x << 9) / z;
									vertexScreenY[v] = cy + (y << 9) / z;
								} else {
									vertexScreenX[v] = -5000;
									project = true;
								}

								if (project || texturedCount > 0) {
									projectSceneX[v] = x;
									projectSceneY[v] = y;
									projectSceneZ[v] = z;
								}
							}

							try {
								draw(bitset, project, hasInput);
							} catch (Exception e) {

							}
						}
					}
				}
			}
		}
	}

	private final void draw(int bitset, boolean projected, boolean hasInput) {
		for (int d = 0; d < maxDepth; d++) {
			depthTriangleCount[d] = 0;
		}

		for (int t = 0; t < triangleCount; t++) {
			if (triangleInfo == null || triangleInfo[t] != -1) {
				int a = triangleVertexA[t];
				int b = triangleVertexB[t];
				int c = triangleVertexC[t];
				int x0 = vertexScreenX[a];
				int x1 = vertexScreenX[b];
				int x2 = vertexScreenX[c];

				if (projected && (x0 == -5000 || x1 == -5000 || x2 == -5000)) {
					projectTriangle[t] = true;
					int depth = ((vertexDepth[a] + vertexDepth[b] + vertexDepth[c]) / 3 + minDepth);
					depthTriangles[depth][depthTriangleCount[depth]++] = t;
				} else {
					if (hasInput && withinTriangle(mouseX, mouseY, vertexScreenY[a], vertexScreenY[b], vertexScreenY[c], x0, x1, x2)) {
						hoveredBitsets[hoverCount++] = bitset;
						hasInput = false;
					}

					if (((x0 - x1) * (vertexScreenY[c] - vertexScreenY[b]) - ((vertexScreenY[a] - vertexScreenY[b]) * (x2 - x1))) > 0) {
						projectTriangle[t] = false;

						if (x0 < 0 || x1 < 0 || x2 < 0 || x0 > Canvas2D.dstXBound || x1 > Canvas2D.dstXBound || x2 > Canvas2D.dstXBound) {
							verifyTriangleBounds[t] = true;
						} else {
							verifyTriangleBounds[t] = false;
						}

						int depth = ((vertexDepth[a] + vertexDepth[b] + vertexDepth[c]) / 3 + minDepth);
						depthTriangles[depth][depthTriangleCount[depth]++] = t;
					}
				}
			}
		}

		if (trianglePriority == null) {
			for (int d = maxDepth - 1; d >= 0; d--) {
				int n = depthTriangleCount[d];
				if (n > 0) {
					int[] triangles = depthTriangles[d];
					for (int t = 0; t < n; t++) {
						drawTriangle(triangles[t]);
					}
				}
			}
		} else {
			for (int p = 0; p < 12; p++) {
				priorityTriangleCount[p] = 0;
				lowPriorityDepth[p] = 0;
			}

			for (int d = maxDepth - 1; d >= 0; d--) {
				int n = depthTriangleCount[d];
				if (n > 0) {
					int[] triangles = depthTriangles[d];
					for (int m = 0; m < n; m++) {
						int t = triangles[m];
						int priority = trianglePriority[t];
						int priorityTriangle = priorityTriangleCount[priority]++;
						priorityTriangles[priority][priorityTriangle] = t;

						if (priority < 10) {
							lowPriorityDepth[priority] += d;
						} else if (priority == 10) {
							normalTrianglePriority[priorityTriangle] = d;
						} else {
							highTrianglePriority[priorityTriangle] = d;
						}
					}
				}
			}

			int minPriority = 0;
			if (priorityTriangleCount[1] > 0 || priorityTriangleCount[2] > 0) {
				minPriority = ((lowPriorityDepth[1] + lowPriorityDepth[2]) / (priorityTriangleCount[1] + priorityTriangleCount[2]));
			}

			int halfPriority = 0;
			if (priorityTriangleCount[3] > 0 || priorityTriangleCount[4] > 0) {
				halfPriority = ((lowPriorityDepth[3] + lowPriorityDepth[4]) / (priorityTriangleCount[3] + priorityTriangleCount[4]));
			}

			int maxPriority = 0;
			if (priorityTriangleCount[6] > 0 || priorityTriangleCount[8] > 0) {
				maxPriority = ((lowPriorityDepth[6] + lowPriorityDepth[8]) / (priorityTriangleCount[6] + priorityTriangleCount[8]));
			}

			int t = 0;
			int triangleCount = priorityTriangleCount[10];
			int[] triangles = priorityTriangles[10];
			int[] priorities = normalTrianglePriority;

			if (t == triangleCount) {
				t = 0;
				triangleCount = priorityTriangleCount[11];
				triangles = priorityTriangles[11];
				priorities = highTrianglePriority;
			}

			int priority;

			if (t < triangleCount) {
				priority = priorities[t];
			} else {
				priority = -1000;
			}

			for (int p = 0; p < 10; p++) {
				while (p == 0) {
					if (priority <= minPriority) {
						break;
					}

					drawTriangle(triangles[t++]);
					if (t == triangleCount && triangles != priorityTriangles[11]) {
						t = 0;
						triangleCount = priorityTriangleCount[11];
						triangles = priorityTriangles[11];
						priorities = highTrianglePriority;
					}
					if (t < triangleCount) {
						priority = priorities[t];
					} else {
						priority = -1000;
					}
				}

				while (p == 3) {
					if (priority <= halfPriority) {
						break;
					}

					drawTriangle(triangles[t++]);

					if (t == triangleCount && triangles != priorityTriangles[11]) {
						t = 0;
						triangleCount = priorityTriangleCount[11];
						triangles = priorityTriangles[11];
						priorities = highTrianglePriority;
					}

					if (t < triangleCount) {
						priority = priorities[t];
					} else {
						priority = -1000;
					}
				}

				while (p == 5 && priority > maxPriority) {
					drawTriangle(triangles[t++]);

					if (t == triangleCount && triangles != priorityTriangles[11]) {
						t = 0;
						triangleCount = priorityTriangleCount[11];
						triangles = priorityTriangles[11];
						priorities = highTrianglePriority;
					}

					if (t < triangleCount) {
						priority = priorities[t];
					} else {
						priority = -1000;
					}
				}

				int n = priorityTriangleCount[p];
				int[] tris = priorityTriangles[p];

				for (int m = 0; m < n; m++) {
					drawTriangle(tris[m]);
				}
			}

			while (priority != -1000) {
				drawTriangle(triangles[t++]);

				if (t == triangleCount && triangles != priorityTriangles[11]) {
					t = 0;
					triangles = priorityTriangles[11];
					triangleCount = priorityTriangleCount[11];
					priorities = highTrianglePriority;
				}

				if (t < triangleCount) {
					priority = priorities[t];
				} else {
					priority = -1000;
				}
			}
		}
	}

	private final void drawTriangle(int index) {
		if (projectTriangle[index]) {
			drawProjectedTriangle(index);
		} else {
			int a = triangleVertexA[index];
			int b = triangleVertexB[index];
			int c = triangleVertexC[index];

			Canvas3D.verifyBounds = verifyTriangleBounds[index];

			if (triangleAlpha == null) {
				Canvas3D.alpha = 0;
			} else {
				Canvas3D.alpha = triangleAlpha[index];
			}

			int type;

			if (triangleInfo == null) {
				type = 0;
			} else {
				type = triangleInfo[index] & 0x3;
			}

			if (type == 0) {
				Canvas3D.fillShadedTriangle(vertexScreenY[a], vertexScreenY[b], vertexScreenY[c], vertexScreenX[a], vertexScreenX[b], vertexScreenX[c], triangleColorA[index], triangleColorB[index], triangleColorC[index]);
			} else if (type == 1) {
				Canvas3D.fillTriangle(vertexScreenY[a], vertexScreenY[b], vertexScreenY[c], vertexScreenX[a], vertexScreenX[b], vertexScreenX[c], palette[triangleColorA[index]]);
			} else if (type == 2) {
				// texture triangle
				int t = triangleInfo[index] >> 2;
				int ta = textureVertexA[t];
				int tb = textureVertexB[t];
				int tc = textureVertexC[t];
				Canvas3D.fillTexturedTriangle(vertexScreenY[a], vertexScreenY[b], vertexScreenY[c], vertexScreenX[a], vertexScreenX[b], vertexScreenX[c], triangleColorA[index], triangleColorB[index], triangleColorC[index], projectSceneX[ta], projectSceneX[tb], projectSceneX[tc], projectSceneY[ta], projectSceneY[tb], projectSceneY[tc], projectSceneZ[ta], projectSceneZ[tb], projectSceneZ[tc], unmodifiedTriangleColor[index]);
			} else if (type == 3) {
				// texture triangle
				int t = triangleInfo[index] >> 2;
				int ta = textureVertexA[t];
				int tb = textureVertexB[t];
				int tc = textureVertexC[t];
				Canvas3D.fillTexturedTriangle(vertexScreenY[a], vertexScreenY[b], vertexScreenY[c], vertexScreenX[a], vertexScreenX[b], vertexScreenX[c], triangleColorA[index], triangleColorA[index], triangleColorA[index], projectSceneX[ta], projectSceneX[tb], projectSceneX[tc], projectSceneY[ta], projectSceneY[tb], projectSceneY[tc], projectSceneZ[ta], projectSceneZ[tb], projectSceneZ[tc], unmodifiedTriangleColor[index]);
			}
		}
	}

	private final void drawProjectedTriangle(int index) {
		int centerX = Canvas3D.centerX;
		int centerY = Canvas3D.centerY;
		int n = 0;

		int a = triangleVertexA[index];
		int b = triangleVertexB[index];
		int c = triangleVertexC[index];

		int aZ = projectSceneZ[a];
		int bZ = projectSceneZ[b];
		int cZ = projectSceneZ[c];

		// It works like this:
		// let us say aZ is < NEAR_Z (50)
		// If cZ is >= NEAR_Z, we're going to approach A to C by howver much we
		// need to to simulate aZ being atleast 50.
		// The same for bZ.
		if (aZ >= Scene.NEAR_Z) {
			tmpX[n] = vertexScreenX[a];
			tmpY[n] = vertexScreenY[a];
			tmpColor[n++] = triangleColorA[index];
		} else {
			int x = projectSceneX[a];
			int y = projectSceneY[a];
			int color = triangleColorA[index];

			if (cZ >= Scene.NEAR_Z) {
				int interpolant = (Scene.NEAR_Z - aZ) * zLerpArray[cZ - aZ];
				tmpX[n] = centerX + ((x + (((projectSceneX[c] - x) * interpolant) >> 16)) << 9) / Scene.NEAR_Z;
				tmpY[n] = centerY + ((y + (((projectSceneY[c] - y) * interpolant) >> 16)) << 9) / Scene.NEAR_Z;
				tmpColor[n++] = color + ((triangleColorC[index] - color) * interpolant >> 16);
			}

			if (bZ >= Scene.NEAR_Z) {
				int interpolant = (Scene.NEAR_Z - aZ) * zLerpArray[bZ - aZ];
				tmpX[n] = (centerX + (x + ((projectSceneX[b] - x) * interpolant >> 16) << 9) / Scene.NEAR_Z);
				tmpY[n] = (centerY + (y + ((projectSceneY[b] - y) * interpolant >> 16) << 9) / Scene.NEAR_Z);
				tmpColor[n++] = color + ((triangleColorB[index] - color) * interpolant >> 16);
			}
		}

		if (bZ >= Scene.NEAR_Z) {
			tmpX[n] = vertexScreenX[b];
			tmpY[n] = vertexScreenY[b];
			tmpColor[n++] = triangleColorB[index];
		} else {
			int x = projectSceneX[b];
			int y = projectSceneY[b];
			int color = triangleColorB[index];

			if (aZ >= Scene.NEAR_Z) {
				int mul = (Scene.NEAR_Z - bZ) * zLerpArray[aZ - bZ];
				tmpX[n] = (centerX + (x + ((projectSceneX[a] - x) * mul >> 16) << 9) / Scene.NEAR_Z);
				tmpY[n] = (centerY + (y + ((projectSceneY[a] - y) * mul >> 16) << 9) / Scene.NEAR_Z);
				tmpColor[n++] = color + ((triangleColorA[index] - color) * mul >> 16);
			}

			if (cZ >= Scene.NEAR_Z) {
				int mul = (Scene.NEAR_Z - bZ) * zLerpArray[cZ - bZ];
				tmpX[n] = (centerX + (x + ((projectSceneX[c] - x) * mul >> 16) << 9) / Scene.NEAR_Z);
				tmpY[n] = (centerY + (y + ((projectSceneY[c] - y) * mul >> 16) << 9) / Scene.NEAR_Z);
				tmpColor[n++] = color + ((triangleColorC[index] - color) * mul >> 16);
			}
		}

		if (cZ >= Scene.NEAR_Z) {
			tmpX[n] = vertexScreenX[c];
			tmpY[n] = vertexScreenY[c];
			tmpColor[n++] = triangleColorC[index];
		} else {
			int x = projectSceneX[c];
			int y = projectSceneY[c];
			int color = triangleColorC[index];

			if (bZ >= Scene.NEAR_Z) {
				int mul = (Scene.NEAR_Z - cZ) * (zLerpArray[bZ - cZ]);

				tmpX[n] = (centerX + (x + (((projectSceneX[b] - x) * mul) >> 16) << 9) / Scene.NEAR_Z);
				tmpY[n] = (centerY + (y + (((projectSceneY[b] - y) * mul) >> 16) << 9) / Scene.NEAR_Z);
				tmpColor[n++] = color + ((triangleColorB[index] - color) * mul >> 16);
			}
			if (aZ >= Scene.NEAR_Z) {
				int mul = (Scene.NEAR_Z - cZ) * zLerpArray[aZ - cZ];
				tmpX[n] = (centerX + (x + (((projectSceneX[a] - x) * mul) >> 16) << 9) / Scene.NEAR_Z);
				tmpY[n] = (centerY + (y + (((projectSceneY[a] - y) * mul) >> 16) << 9) / Scene.NEAR_Z);
				tmpColor[n++] = color + ((triangleColorA[index] - color) * mul >> 16);
			}
		}

		int x0 = tmpX[0];
		int x1 = tmpX[1];
		int x2 = tmpX[2];

		int y0 = tmpY[0];
		int y1 = tmpY[1];
		int y2 = tmpY[2];

		if (((x0 - x1) * (y2 - y1) - (y0 - y1) * (x2 - x1)) > 0) {
			Canvas3D.verifyBounds = false;

			if (n == 3) {
				if (x0 < 0 || x1 < 0 || x2 < 0 || x0 > Canvas2D.dstXBound || x1 > Canvas2D.dstXBound || x2 > Canvas2D.dstXBound) {
					Canvas3D.verifyBounds = true;
				}

				int type;

				if (triangleInfo == null) {
					type = 0;
				} else {
					type = triangleInfo[index] & 0x3;
				}

				if (type == 0) {
					Canvas3D.fillShadedTriangle(y0, y1, y2, x0, x1, x2, tmpColor[0], tmpColor[1], tmpColor[2]);
				} else if (type == 1) {
					Canvas3D.fillTriangle(y0, y1, y2, x0, x1, x2, (palette[(triangleColorA[index])]));
				} else if (type == 2) {
					int t = triangleInfo[index] >> 2;
					int ta = textureVertexA[t];
					int tb = textureVertexB[t];
					int tc = textureVertexC[t];
					Canvas3D.fillTexturedTriangle(y0, y1, y2, x0, x1, x2, tmpColor[0], tmpColor[1], tmpColor[2], projectSceneX[ta], projectSceneX[tb], projectSceneX[tc], projectSceneY[ta], projectSceneY[tb], projectSceneY[tc], projectSceneZ[ta], projectSceneZ[tb], projectSceneZ[tc], unmodifiedTriangleColor[index]);
				} else if (type == 3) {
					int t = triangleInfo[index] >> 2;
					int ta = textureVertexA[t];
					int tb = textureVertexB[t];
					int tc = textureVertexC[t];
					Canvas3D.fillTexturedTriangle(y0, y1, y2, x0, x1, x2, triangleColorA[index], triangleColorA[index], triangleColorA[index], projectSceneX[ta], projectSceneX[tb], projectSceneX[tc], projectSceneY[ta], projectSceneY[tb], projectSceneY[tc], projectSceneZ[ta], projectSceneZ[tb], projectSceneZ[tc], unmodifiedTriangleColor[index]);
				}
			}

			if (n == 4) {
				if (x0 < 0 || x1 < 0 || x2 < 0 || x0 > Canvas2D.dstXBound || x1 > Canvas2D.dstXBound || x2 > Canvas2D.dstXBound || tmpX[3] < 0 || tmpX[3] > Canvas2D.dstXBound) {
					Canvas3D.verifyBounds = true;
				}

				int type;

				if (triangleInfo == null) {
					type = 0;
				} else {
					type = triangleInfo[index] & 0x3;
				}

				if (type == 0) {
					Canvas3D.fillShadedTriangle(y0, y1, y2, x0, x1, x2, tmpColor[0], tmpColor[1], tmpColor[2]);
					Canvas3D.fillShadedTriangle(y0, y2, tmpY[3], x0, x2, tmpX[3], tmpColor[0], tmpColor[2], tmpColor[3]);
				} else if (type == 1) {
					int rgb = palette[triangleColorA[index]];
					Canvas3D.fillTriangle(y0, y1, y2, x0, x1, x2, rgb);
					Canvas3D.fillTriangle(y0, y2, tmpY[3], x0, x2, tmpX[3], rgb);
				} else if (type == 2) {
					int t = triangleInfo[index] >> 2;
					int ta = textureVertexA[t];
					int tb = textureVertexB[t];
					int tc = textureVertexC[t];
					Canvas3D.fillTexturedTriangle(y0, y1, y2, x0, x1, x2, tmpColor[0], tmpColor[1], tmpColor[2], projectSceneX[ta], projectSceneX[tb], projectSceneX[tc], projectSceneY[ta], projectSceneY[tb], projectSceneY[tc], projectSceneZ[ta], projectSceneZ[tb], projectSceneZ[tc], unmodifiedTriangleColor[index]);
					Canvas3D.fillTexturedTriangle(y0, y2, tmpY[3], x0, x2, tmpX[3], tmpColor[0], tmpColor[2], tmpColor[3], projectSceneX[ta], projectSceneX[tb], projectSceneX[tc], projectSceneY[ta], projectSceneY[tb], projectSceneY[tc], projectSceneZ[ta], projectSceneZ[tb], projectSceneZ[tc], unmodifiedTriangleColor[index]);
				} else if (type == 3) {
					int t = triangleInfo[index] >> 2;
					int ta = textureVertexA[t];
					int tb = textureVertexB[t];
					int tc = textureVertexC[t];
					Canvas3D.fillTexturedTriangle(y0, y1, y2, x0, x1, x2, triangleColorA[index], triangleColorA[index], triangleColorA[index], projectSceneX[ta], projectSceneX[tb], projectSceneX[tc], projectSceneY[ta], projectSceneY[tb], projectSceneY[tc], projectSceneZ[ta], projectSceneZ[tb], projectSceneZ[tc], unmodifiedTriangleColor[index]);
					Canvas3D.fillTexturedTriangle(y0, y2, tmpY[3], x0, x2, tmpX[3], triangleColorA[index], triangleColorA[index], triangleColorA[index], projectSceneX[ta], projectSceneX[tb], projectSceneX[tc], projectSceneY[ta], projectSceneY[tb], projectSceneY[tc], projectSceneZ[ta], projectSceneZ[tb], projectSceneZ[tc], unmodifiedTriangleColor[index]);
				}
			}
		}
	}

	public static final boolean withinTriangle(int x, int y, int y0, int y1, int y2, int x0, int x1, int x2) {
		if (y < y0 && y < y1 && y < y2) {
			return false;
		}
		if (y > y0 && y > y1 && y > y2) {
			return false;
		}
		if (x < x0 && x < x1 && x < x2) {
			return false;
		}
		if (x > x0 && x > x1 && x > x2) {
			return false;
		}
		return true;
	}
}