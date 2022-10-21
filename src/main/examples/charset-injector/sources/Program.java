class Program {
    public String convert(final String source) {
        final InputStream stream = IOUtils.toInputStream(source);
        final String result = IOUtils.toString(stream);
        stream.close();
        return result;
    }
}