#include <jni.h>
#include <string>
#ifndef MAX
#define MAX(a, b) ({__typeof__(a) _a = (a); __typeof__(b) _b = (b); _a > _b ? _a : _b; })
#define MIN(a, b) ({__typeof__(a) _a = (a); __typeof__(b) _b = (b); _a < _b ? _a : _b; })
#endif

static const int kMaxChannelValue = 262143;

void ConvertYUV420ToARGB8888(const uint8_t* const yData,
                             const uint8_t* const uData,
                             const uint8_t* const vData, uint32_t* const output,
                             const int width, const int height,
                             const int y_row_stride, const int uv_row_stride,
                             const int uv_pixel_stride);


static inline uint32_t YUV2RGB(int nY, int nU, int nV) {
    nY -= 16;
    nU -= 128;
    nV -= 128;
    if (nY < 0) nY = 0;

    // This is the floating point equivalent. We do the conversion in integer
    // because some Android devices do not have floating point in hardware.
    // nR = (int)(1.164 * nY + 2.018 * nU);
    // nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
    // nB = (int)(1.164 * nY + 1.596 * nV);

    int nR = (int)(1192 * nY + 1634 * nV);
    int nG = (int)(1192 * nY - 833 * nV - 400 * nU);
    int nB = (int)(1192 * nY + 2066 * nU);

    nR = MIN(kMaxChannelValue, MAX(0, nR));
    nG = MIN(kMaxChannelValue, MAX(0, nG));
    nB = MIN(kMaxChannelValue, MAX(0, nB));

    nR = (nR >> 10) & 0xff;
    nG = (nG >> 10) & 0xff;
    nB = (nB >> 10) & 0xff;

    return 0xff000000 | (nR << 16) | (nG << 8) | nB;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_harsimar_hyperworkscheckout_ImageUtils_convertYUV420ToARGB8888(
        JNIEnv* env, jclass clazz, jbyteArray y, jbyteArray u, jbyteArray v,
        jintArray output, jint width, jint height, jint y_row_stride,
        jint uv_row_stride, jint uv_pixel_stride, jboolean halfSize) {
    jboolean inputCopy = JNI_FALSE;
    jbyte* const y_buff = env->GetByteArrayElements(y, &inputCopy);
    jboolean outputCopy = JNI_FALSE;
    jint* const o = env->GetIntArrayElements(output, &outputCopy);

    jbyte* const u_buff = env->GetByteArrayElements(u, &inputCopy);
        jbyte* const v_buff = env->GetByteArrayElements(v, &inputCopy);

        ConvertYUV420ToARGB8888(
                reinterpret_cast<uint8_t*>(y_buff), reinterpret_cast<uint8_t*>(u_buff),
                reinterpret_cast<uint8_t*>(v_buff), reinterpret_cast<uint32_t*>(o),
                width, height, y_row_stride, uv_row_stride, uv_pixel_stride);

        env->ReleaseByteArrayElements(u, u_buff, JNI_ABORT);
        env->ReleaseByteArrayElements(v, v_buff, JNI_ABORT);
    env->ReleaseByteArrayElements(y, y_buff, JNI_ABORT);
    env->ReleaseIntArrayElements(output, o, 0);
}

void ConvertYUV420ToARGB8888(const uint8_t* const yData,
                             const uint8_t* const uData,
                             const uint8_t* const vData, uint32_t* const output,
                             const int width, const int height,
                             const int y_row_stride, const int uv_row_stride,
                             const int uv_pixel_stride) {
    uint32_t* out = output;

    for (int y = 0; y < height; y++) {
        const uint8_t* pY = yData + y_row_stride * y;

        const int uv_row_start = uv_row_stride * (y >> 1);
        const uint8_t* pU = uData + uv_row_start;
        const uint8_t* pV = vData + uv_row_start;

        for (int x = 0; x < width; x++) {
            const int uv_offset = (x >> 1) * uv_pixel_stride;
            *out++ = YUV2RGB(pY[x], pU[uv_offset], pV[uv_offset]);
        }
    }
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_harsimar_hyperworkscheckout_ClassifierActivity_stringFromJNI(JNIEnv *env,
                                                                              jobject instance) {

    std::string hello = "Hello from C++";

    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_harsimar_hyperworkscheckout_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
