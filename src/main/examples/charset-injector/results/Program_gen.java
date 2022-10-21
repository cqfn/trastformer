class Program {
    public String convert(final String source) {
        final InputStream stream = IOUtils.toInputStream(source, "UTF-8");
        final String result = IOUtils.toString(stream, "UTF-8");
        stream.close();
        return result;
    }
}
