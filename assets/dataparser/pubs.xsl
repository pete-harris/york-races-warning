<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" indent="no"/>
	<xsl:template match="/">
		[
		<xsl:for-each select="*/*/Placemark">
			<xsl:text>{ "name": "</xsl:text>
			<xsl:value-of select="normalize-space(name)" />
			<xsl:text>", </xsl:text>
			<xsl:call-template name="splitlat">
				<xsl:with-param select="Point/coordinates" name="input" />
			</xsl:call-template>
			<xsl:text>}</xsl:text>
			<xsl:if test="position() &lt; last()">
				<xsl:text>, </xsl:text>
			</xsl:if>
			<xsl:text>
			</xsl:text>
		</xsl:for-each>
		]
	</xsl:template>

	<xsl:template name="splitlat">
		<xsl:param name="input" />
		<xsl:text>"lon": </xsl:text>
		<xsl:value-of select="normalize-space(substring-before($input, ','))"/>
		<xsl:text>, "lat": </xsl:text>
		<xsl:value-of select="normalize-space(substring-before(substring-after($input,','), ','))"/>
	</xsl:template>
</xsl:stylesheet>


