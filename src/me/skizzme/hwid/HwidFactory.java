package me.skizzme.hwid;

import me.skizzme.hwid.component.HwidComponent;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.prng.FixedSecureRandom;
import org.bouncycastle.util.encoders.Base64;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public final class HwidFactory
{

    private String buffer = null;

    public HwidFactory()
    {
    }

    public HwidFactory insert(final HwidComponent component)
    {
        this.buffer += String.format("{%s:%s}", component.id(), component.lookup());
        return this;
    }

    public HwidFactory insert(final HwidComponent... components)
    {
        for (final HwidComponent component : components)
        {
            this.insert(component);
        }

        return this;
    }

    private byte[] merge(final byte[] array1, final byte[] array2)
    {
        final byte[] result = new byte[array1.length + array2.length];

        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);

        return result;
    }

    public String generate(final boolean useSalt)
    {
        final Digest digest = new SHA3Digest(512, CryptoServicePurpose.ANY);

        byte[] input = this.buffer.getBytes(StandardCharsets.UTF_8);
        if (useSalt)
        {
            final SecureRandom random = new FixedSecureRandom(input);
            final byte[] salt = new byte[16];
            random.nextBytes(salt);

            input = this.merge(input, salt);
        }

        digest.update(input, 0, input.length);

        final byte[] output = new byte[digest.getDigestSize()];
        digest.doFinal(output, 0);

        return Base64.toBase64String(output);
    }

}
